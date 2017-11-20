package Client;

import Support.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);
  private static DataOutputStream outToServer;
  private static Scanner inFromServer;

  // String literals
  private static String usrNameMsg = "Type your username:";
  private static String usrNameErr = "Username must contain only alphanumeric characters and be between 3 to 20 characters long:";
  private static String usrNameRex = "[0-9a-zA-Z]{3,20}";
  private static String grpNameMsg = "Type the group name:";
  private static String grpNameErr = "Groupname must contain only alphanumeric characters and be between 3 to 20 characters long:";
  private static String grpNameRex = "[0-9a-zA-Z]{3,20}";
  private static String subjectMsg = "Type the subject:";
  private static String subjectErr = "Subject cannot contain character ':' and must be between 1 to 255 characters long:";
  private static String subjectRex = "[^:]{1,255}";
  private static String messageMsg = "Type your message:";
  private static String messageErr = "Message cannot contain character ':' and must be between 1 to 3000 characters long:";
  private static String messageRex = "[^:]{1,3000}";
  private static String recipntMsg = "Type the recipient username:";

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

  // Returns an integer from the user input between 1 and the specified range
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

  // Returns a string from user input that maches the specified regex
  private static String getString(String message, String error, String regex) {
    String string;
    System.out.println(message);
    while (true) {
      if ((string = keyboard.nextLine()).matches(regex)) return string;
      else System.out.println(error);
    }
  }

  private static boolean signIn() throws IOException {
    sendData(new String[]{"I", getString(usrNameMsg, usrNameErr, usrNameRex)});
    return receiveResponse();
  }

  private static boolean signUp() throws IOException {
    sendData(new String[]{"R", getString(usrNameMsg, usrNameErr, usrNameRex)});
    return receiveResponse();
  }

  private static void sendToClient() throws IOException {
    String recipnt = getString(recipntMsg, usrNameErr, usrNameRex);
    String subject = getString(subjectMsg, subjectErr, subjectRex);
    String message = getString(messageMsg, messageErr, messageRex);
    sendData(new String[]{"S", recipnt, subject, message});
    receiveResponse();
  }

  private static void sendToGroup() throws IOException {
    String grpname = getString(grpNameMsg, grpNameErr, grpNameRex);
    String subject = "Group " + grpname;
    String message = getString(messageMsg, messageErr, messageRex);
    sendData(new String[]{"T", grpname, subject, message});
    receiveResponse();
  }

  private static void readMessages() throws IOException {
    sendData(new String[]{"M"});
    receiveResponseMessages();
  }

  private static void createGroup() throws IOException {
    sendData(new String[]{"G", getString(grpNameMsg, grpNameErr, grpNameRex)});
    receiveResponse();
  }

  private static void joinGroup() throws IOException {
    sendData(new String[]{"J", getString(grpNameMsg, grpNameErr, grpNameRex)});
    receiveResponse();
  }

  private static void leaveGroup() throws IOException {
    sendData(new String[]{"L", getString(grpNameMsg, grpNameErr, grpNameRex)});
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
    System.out.println("Error: Unknown Response");
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

    try (Socket socket = new Socket(args[0], Integer.parseInt(args[1]))) {
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
