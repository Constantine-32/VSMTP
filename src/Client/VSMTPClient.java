package Client;

import Support.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);
  private static DataOutputStream outToServer;
  private static Scanner inFromServer;

  private static void loginMenu() {
    System.out.println("Available options:");
    System.out.println("\t1. Sign In.");
    System.out.println("\t2. Sign Up.");
    System.out.println("\t3. Exit.");
    System.out.println("Choose an option:");
  }

  private static void mainMenu() {
    System.out.println("Available options:");
    System.out.println("\t1. Send message to a client.");
    System.out.println("\t2. Send message to a group.");
    System.out.println("\t3. Read pending messages.");
    System.out.println("\t4. Create group.");
    System.out.println("\t5. Join group.");
    System.out.println("\t6. Leave group.");
    System.out.println("\t7. Delete account.");
    System.out.println("\t8. Sign out.");
    System.out.println("Choose an option:");
  }

  private static int getOption(int options) {
    int option;
    do {
      try {
        option = Integer.parseInt(keyboard.nextLine());
        if (1 <= option && option <= options) return option;
        else System.out.println("Invalid option!");
      } catch (NumberFormatException e) {
        System.out.println("Invalid option!");
      }
    } while (true);
  }

  private static String getString(String regex) {
    String string;
    do {
      if ((string = keyboard.nextLine()).matches(regex)) return string;
      else System.out.println("Invalid String!");
    } while (true);
  }

  private static boolean signIn() throws IOException {
    System.out.println("Type your username:");
    outToServer.writeBytes("I:" + getString("[0-9a-zA-Z]{4,20}") + '\n');
    return processResponse();
  }

  private static boolean signUp() throws IOException {
    System.out.println("Type your username:");
    outToServer.writeBytes("R:" + getString("[0-9a-zA-Z]{4,20}") + '\n');
    return processResponse();
  }

  private static boolean sendToClient() throws IOException {
    String sendData;
    System.out.println("Type the recipient username:");
    sendData = "S:" + getString("[0-9a-zA-Z]{4,20}");
    System.out.println("Type the subject:");
    sendData += ":" + getString("[^:]{1,255}");
    System.out.println("Type your message:");
    outToServer.writeBytes(sendData + ":" + getString("[^:]{1,3000}") + '\n');
    return processResponse();
  }

  private static boolean sendToGroup() throws IOException {
    String sendData;
    System.out.println("Type the group name:");
    String groupName = getString("[0-9a-zA-Z]{4,20}");
    sendData = "T:" + groupName + ":Group " + groupName;
    System.out.println("Type your message:");
    outToServer.writeBytes(sendData + ":" + getString("[^:]{1,3000}") + '\n');
    return processResponse();
  }

  private static boolean readMessages() throws IOException {
    outToServer.writeBytes("M\n");
    return processResponseMessages();
  }

  private static boolean createGroup() throws IOException {
    System.out.println("Type the group name:");
    outToServer.writeBytes("G:" + getString("[0-9a-zA-Z]{2,20}") + '\n');
    return processResponse();
  }

  private static boolean joinGroup() throws IOException {
    System.out.println("Type the group name:");
    outToServer.writeBytes("J:" + getString("[0-9a-zA-Z]{2,20}") + '\n');
    return processResponse();
  }

  private static boolean leaveGroup() throws IOException {
    System.out.println("Type the group name:");
    outToServer.writeBytes("L:" + getString("[0-9a-zA-Z]{2,20}") + '\n');
    return processResponse();
  }

  private static boolean deleteAccount() throws IOException {
    outToServer.writeBytes("D\n");
    return processResponse();
  }

  private static boolean processResponse() {
    Scanner response = new Scanner(inFromServer.nextLine()).useDelimiter(":");
    String result = response.next();
    if (result.equals("OK")) {
      System.out.println("Success: " + response.next());
      return true;
    }
    if (result.equals("KO")) {
      System.out.println("Error: " + response.next());
      return false;
    }
    System.out.println("Unknown Response");
    return false;
  }

  private static boolean processResponseMessages() {
    Scanner response = new Scanner(inFromServer.nextLine()).useDelimiter(":");
    String result = response.next();
    if (result.equals("KO")) {
      System.out.println("Error: " + response.next());
      return false;
    }
    LinkedList<Message> messageList = new LinkedList<>();
    while (response.hasNext()) {
      messageList.add(new Message(response.next(), response.next(), response.next()));
    }
    int count = 1;
    for (Message message : messageList) {
      System.out.println("Message " + (count++) + " out of " + messageList.size());
      System.out.println(message.toString());
    }
    return true;
  }

  public static void main(String[] args) throws Exception {
    boolean exit = false;
    boolean loggedIn = false;

    try (Socket socket = new Socket("localhost", 1234)) {
      outToServer = new DataOutputStream(socket.getOutputStream());
      inFromServer = new Scanner(socket.getInputStream());

      while (!exit) {
        loginMenu();
        switch (getOption(3)) {
        case 1: loggedIn = signIn(); break;
        case 2: loggedIn = signUp(); break;
        case 3: exit = true;
        }
        while (loggedIn) {
          mainMenu();
          switch (getOption(8)) {
          case 1: sendToClient(); break;
          case 2: sendToGroup(); break;
          case 3: readMessages(); break;
          case 4: createGroup(); break;
          case 5: joinGroup(); break;
          case 6: leaveGroup(); break;
          case 7: deleteAccount();
          case 8: loggedIn = false;
          }
        }
      }

    } catch (IOException e) {
      System.out.println("Server connection error: " + e.getMessage());
    }
  }
}
