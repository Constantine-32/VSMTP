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

  private static int getOption(int range) {
    int option;
    while (true) {
      try {
        option = Integer.parseInt(keyboard.nextLine());
        if (1 <= option && option <= range) return option;
        else System.out.println("Invalid option!");
      } catch (NumberFormatException e) {
        System.out.println("Invalid option!");
      }
    }
  }

  private static String getString(String regex) {
    String string;
    while (true) {
      if ((string = keyboard.nextLine()).matches(regex)) return string;
      else System.out.println("Invalid String!");
    }
  }

  private static boolean signIn() throws IOException {
    System.out.println("Type your username:");
    sendData(new String[]{"I", getString("[0-9a-zA-Z]{4,20}")});
    return receiveResponse();
  }

  private static boolean signUp() throws IOException {
    System.out.println("Type your username:");
    sendData(new String[]{"R", getString("[0-9a-zA-Z]{4,20}")});
    return receiveResponse();
  }

  private static void sendToClient() throws IOException {
    System.out.println("Type the recipient username:");
    String recipient = getString("[0-9a-zA-Z]{4,20}");
    System.out.println("Type the subject:");
    String subject = getString("[^:]{1,255}");
    System.out.println("Type your message:");
    String message = getString("[^:]{1,3000}");
    sendData(new String[]{"S", recipient, subject, message});
    receiveResponse();
  }

  private static void sendToGroup() throws IOException {
    System.out.println("Type the group name:");
    String group = getString("[0-9a-zA-Z]{4,20}");
    System.out.println("Type your message:");
    String message = getString("[^:]{1,3000}");
    sendData(new String[]{"T", group, "Group " + group, message});
    receiveResponse();
  }

  private static void readMessages() throws IOException {
    sendData(new String[]{"M"});
    receiveResponseMessages();
  }

  private static void createGroup() throws IOException {
    System.out.println("Type the group name:");
    sendData(new String[]{"G", getString("[0-9a-zA-Z]{2,20}")});
    receiveResponse();
  }

  private static void joinGroup() throws IOException {
    System.out.println("Type the group name:");
    sendData(new String[]{"J", getString("[0-9a-zA-Z]{2,20}")});
    receiveResponse();
  }

  private static void leaveGroup() throws IOException {
    System.out.println("Type the group name:");
    sendData(new String[]{"L", getString("[0-9a-zA-Z]{2,20}")});
    receiveResponse();
  }

  private static void deleteAccount() throws IOException {
    sendData(new String[]{"D"});
    receiveResponse();
  }

  private static void sendData(String[] data) throws IOException {
    outToServer.writeBytes(String.join(":", data) + '\n');
  }

  private static boolean receiveResponse() {
    String[] response = inFromServer.nextLine().split(":");
    if (response[0].equals("OK")) {
      System.out.println("Success: " + response[1]);
      return true;
    }
    if (response[0].equals("KO")) {
      System.out.println("Error: " + response[1]);
      return false;
    }
    System.out.println("Unknown Response");
    return false;
  }

  private static void receiveResponseMessages() {
    String[] response = inFromServer.nextLine().split(":");
    if (response[0].equals("OK")) {
      int messages = (response.length - 1) / 3;
      for (int i = 0; i < messages; i++) {
        System.out.println("Message " + (i+1) + " out of " + messages);
        System.out.println(new Message(response[(i * 3) + 1], response[(i * 3) + 2], response[(i * 3) + 3]).toString());
      }
    } else System.out.println("Error: " + response[1]);
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
