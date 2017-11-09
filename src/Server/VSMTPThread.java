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
        System.out.println("Thread[" + threadId + "] " + clientData);
        String response = processRequest(clientData);
        outToClient.writeBytes(response + "\n");
      }
      socket.close();
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    System.out.println("Thread[" + threadId + "] CLOSED");
  }

  private String processRequest(String clientData) {
    Scanner data = new Scanner(clientData).useDelimiter(":");
    switch (data.next()) {
    case "R": return register(data);
    case "D": return unregister();
    case "S": return send(data);
    case "M": return readMessages();
    case "G": return createGroup(data);
    case "J": return joinGroup(data);
    default: return "KO:Unknown code";
    }
  }

  private String register(Scanner data) {
    client = data.next();
    if (userTable.containsKey(client)) return "KO:Username already exists";
    userTable.put(client, new Client(client));
    return "OK:Registered successfully";
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
    if (result.isEmpty()) return "KO:No new messages";
    for (Message m : result) m.read();
    return "OK:"+result.toString();
  }

  private String createGroup(Scanner data) {
    return "KO:Not implemented";
  }

  private String joinGroup(Scanner data) {
    return "KO:Not implemented";
  }
}
