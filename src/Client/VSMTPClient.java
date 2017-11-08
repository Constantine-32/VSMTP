package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);

  private static void mainMenu() {
    System.out.println("Available options:");
    System.out.println("\t1. Register your account.");
    System.out.println("\t2. Delete your account.");
    System.out.println("\t3. Send a message.");
    System.out.println("\t4. Read pending messages.");
    System.out.println("\t5. Create a group.");
    System.out.println("\t6. Join a group.");
    System.out.println("\t7. Exit.");
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

  public static void main(String[] args) throws Exception {
    int option;
    String sendData = "";
    Socket socket;
    try {
      socket = new Socket("localhost", 1234);
    } catch (IOException e) {
      System.out.println("Couldn't connect to the server: " + e.getMessage());
      return;
    }
    DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
    Scanner inFromServer = new Scanner(socket.getInputStream());

    mainMenu();
    while ((option = getOption(7)) != 7) {
      switch (option) {
      case 1:
        sendData = "R";
        System.out.println("Type your username:");
        sendData += ":" + getString("[0-9a-zA-Z]{4,20}");
        break;
      case 2:
        sendData = "D";
        break;
      case 3:
        sendData = "S";
        System.out.println("Type the recipient username:");
        sendData += ":" + getString("[0-9a-zA-Z]{4,20}");
        System.out.println("Type the subject:");
        sendData += ":" + getString("[^:]{1,255}");
        System.out.println("Type your message:");
        sendData += ":" + getString("[^:]{1,3000}");
        break;
      case 4:
        sendData = "M";
        break;
      case 5:
        sendData = "G";
        System.out.println("Type the group name:");
        sendData += ":" + getString("[0-9a-zA-Z]{4,20}");
        break;
      case 6:
        sendData = "J";
        System.out.println("Type the group name:");
        sendData += ":" + getString("[0-9a-zA-Z]{4,20}");
        break;
      }
      outToServer.writeBytes(sendData + '\n');
      System.out.println("Server response: " + inFromServer.nextLine());
      mainMenu();
    }

    socket.close();
  }
}
