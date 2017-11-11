package Client;

import Support.Message;

import java.io.*;
import java.net.*;
import java.util.*;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);

  private static void mainMenu() {
    System.out.println("Available options:");
    System.out.println("\t1. Register your account.");
    System.out.println("\t2. Delete your account.");
    System.out.println("\t3. Send a message to a client.");
    System.out.println("\t4. Send a message to a group.");
    System.out.println("\t5. Read pending messages.");
    System.out.println("\t6. Create a group.");
    System.out.println("\t7. Join a group.");
    System.out.println("\t8. Exit.");
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

  private static String getSendData(int option) {
    String sendData;
    switch (option) {
    case 1:
      System.out.println("Type your username:");
      return "R:" + getString("[0-9a-zA-Z]{4,20}");
    case 2:
      return "D";
    case 3:
      System.out.println("Type the recipient username:");
      sendData = "S:" + getString("[0-9a-zA-Z]{4,20}");
      System.out.println("Type the subject:");
      sendData += ":" + getString("[^:]{1,255}");
      System.out.println("Type your message:");
      return sendData + ":" + getString("[^:]{1,3000}");
    case 4:
      System.out.println("Type the group name:");
      String groupName = getString("[0-9a-zA-Z]{4,20}");
      sendData = "T:" + groupName + ":Group " + groupName;
      System.out.println("Type your message:");
      return sendData + ":" + getString("[^:]{1,3000}");
    case 5:
      return "M";
    case 6:
      System.out.println("Type the group name:");
      return "G:" + getString("[0-9a-zA-Z]{4,20}");
    case 7:
      System.out.println("Type the group name:");
      return "J:" + getString("[0-9a-zA-Z]{4,20}");
    default:
      return "";
    }
  }

  private static void processResponse(String response, int option) {
    Scanner data = new Scanner(response).useDelimiter(":");
    switch (option) {
    case 1:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    case 2:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    case 3:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    case 4:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    case 5:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else {
        LinkedList<Message> messageList = new LinkedList<>();
        while (data.hasNext()) {
          messageList.add(new Message(data.next(), data.next(), data.next()));
        }
        int count = 1;
        for (Message message : messageList) {
          System.out.println("Message " + (count++) + " out of " + messageList.size());
          System.out.println(message.toString());
        }
      }
      break;
    case 6:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    case 7:
      if (data.next().equals("KO"))
        System.out.println("Error: " + data.next());
      else
        System.out.println("Success: " + data.next());
      break;
    default:
      System.out.println("Uknown Response");
    }
  }

  public static void main(String[] args) throws Exception {
    int option;
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
    while ((option = getOption(7)) != 8) {
      outToServer.writeBytes(getSendData(option) + '\n');
      processResponse(inFromServer.nextLine(), option);
      mainMenu();
    }

    socket.close();
  }
}
