package Client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);

  private static void mainMenu() {
    System.out.println("Avaliable options:");
    System.out.println("\t1. Register your account.");
    System.out.println("\t2. Delete your account.");
    System.out.println("\t3. Send a message.");
    System.out.println("\t4. Read pending messages.");
    System.out.println("\t5. Exit.");
    System.out.println("Choose an option:");
  }

  private static int getOption(int options) {
    int option = -1;
    boolean valid = false;

    while (!valid) {
      try {
        option = Integer.parseInt(keyboard.nextLine());
        if (1 <= option && option <= options) valid = true;
        else System.out.println("Invalid option!");
      } catch (NumberFormatException e) {
        System.out.println("Invalid option!");
      }
    }

    return option;
  }

  public static void main(String[] args) throws Exception {
    int option;
    String sendData = "";
    Socket socket = new Socket("localhost", 1234);
    DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
    Scanner inFromServer = new Scanner(socket.getInputStream());

    mainMenu();
    while ((option = getOption(5)) != 5) {
      switch (option) {
      case 1:
        sendData = "R:";
        System.out.println("Type your username:");
        sendData += keyboard.nextLine();
        break;
      case 2:
        sendData = "D";
        break;
      case 3:
        sendData = "S:";
        System.out.println("Type the recipient username:");
        sendData += keyboard.nextLine()+":";
        System.out.println("Type your message:");
        sendData += keyboard.nextLine();
        break;
      case 4:
        sendData = "M";
        break;
      }
      outToServer.writeBytes(sendData + '\n');
      System.out.println("Server response: "+inFromServer.nextLine());
      mainMenu();
    }

    socket.close();
  }
}
