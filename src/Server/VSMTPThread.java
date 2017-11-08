package Server;

import java.io.*;
import java.net.*;
import java.util.*;

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
    case "S":
      response = send(data);
      break;
    case "M":
      response = readMessages();
      break;
    case "G":
      //createGroup(data);
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
    if (userTable.containsKey(client)) return "KO:Username already exists";
    else {
      userTable.put(client, new Client(client));
      return "OK:Registered successfully";
    }
  }

  private String unregister() {
    userTable.get(client).setRegistered(false);
    return "OK:Unregistered successfully";
  }

  private String send(Scanner data) {
    String recipient = data.next();
    String subject = data.next();
    String message = data.next();
    if (userTable.containsKey(recipient)) {
      userTable.get(recipient).addMessage(new Message(client, subject, message));
      return "OK:Message sent";
    } else return "KO:Message NOT sent, unknown recipient";
  }

  private String readMessages() {
    List<Message> result = userTable.get(client).getUnreadMessages();
    if (result.isEmpty()) return "No new messages";
    for (Message m : result) m.read();
    return result.toString();

  }
}
