package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VSMTPThread extends Thread {
  private static int nextId = 1;
  private int threadId;

  private static Map<String, Client> userTable = new HashMap<>();
  private Socket socket;
  private String client;

  VSMTPThread(Socket socket) {
    this.socket = socket;
    threadId = nextId++;
    System.out.println("Thread[" + threadId + "] STARTED");
  }

  @Override
  public synchronized void run() {
    try {
      Scanner inFromClient = new Scanner(socket.getInputStream());
      DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
      while (inFromClient.hasNext()) {
        String clientData = inFromClient.nextLine();
        // Client Data Processing
        System.out.println("Thread[" + threadId + "] " + clientData);
        outToClient.writeBytes(processRequest(clientData) + "\n");
      }
      socket.close();
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    System.out.println("Thread[" + threadId + "] CLOSED");
  }

  private String processRequest(String clientData) {
    Scanner data = new Scanner(clientData).useDelimiter(":");
    String response = "";
    switch (data.next()) {
    case "R":
      response = register(data);
      break;
    case "D":
      response = unregister();
      break;
    case "E":
      response = send(data);
      break;
    case "G":
      //createGroup(data);
      break;
    case "M":
      response = readMessages();
      break;
    case "J":
      //addToGroup(data);
      break;
    default:
    }
    return response;
  }

  private String register(Scanner data) {
    client = data.next();
    userTable.put(client, new Client(client));
//    System.out.println(userTable);
    return "OK:Register succesful";
  }

  private String unregister() {
    userTable.get(client).setRegistered(false);
    return "OK:Unregistered succesful";
  }

  private String send(Scanner data) {
    String recipient = data.next();
    String subject = data.next();
    String message = data.next();
    userTable.get(recipient).addMessage(new Message(client, subject, message));
//    System.out.println(recipient);
    return "OK:Message send";
  }

  private String readMessages() {
    return userTable.get(client).getUnreadMessages().toString();
  }
}
