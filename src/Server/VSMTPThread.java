package Server;

import Support.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class VSMTPThread extends Thread {
  private static int nextId = 1;
  private int threadId;

  private static Map<String, Client> clientTable = new HashMap<>();
  private static Map<String, Group> groupTable = new HashMap<>();

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
        String fromClient = inFromClient.nextLine();
        System.out.println("Thread[" + threadId + "] From Client: \"" + fromClient + '\"');
        String toClient = processRequest(fromClient);
        outToClient.writeBytes(toClient + "\n");
        System.out.println("Thread[" + threadId + "] To Client: \"" + toClient + '\"');
      }
      socket.close();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    System.out.println("Thread[" + threadId + "] CLOSED");
  }

  private String processRequest(String clientData) {
    Scanner data = new Scanner(clientData).useDelimiter(":");
    switch (data.next()) {
    case "R": return register(data);
    case "D": return unregister();
    case "S": return send(data);
    case "T": return sendGroup(data);
    case "M": return readMessages();
    case "G": return createGroup(data);
    case "J": return joinGroup(data);
    default: return "KO:Unknown code";
    }
  }

  private String register(Scanner data) {
    client = data.next();
    if (clientTable.containsKey(client)) return "KO:Username already exists";
    clientTable.put(client, new Client(client));
    return "OK:Registered successfully";
  }

  private String unregister() {
    clientTable.get(client).setActive(false);
    return "OK:Unregistered successfully";
  }

  private String send(Scanner data) {
    String recipient = data.next();
    String subject = data.next();
    String message = data.next();
    if (clientTable.containsKey(recipient)) {
      clientTable.get(recipient).addMessage(new Message(client, subject, message));
      return "OK:Message sent successfully";
    } else return "KO:Message NOT sent, unknown recipient";
  }

  private String sendGroup(Scanner data) {
    String group = data.next();
    String subject = data.next();
    String message = data.next();
    if (!groupTable.containsKey(group)) return "KO:Group doesn't exists";
    if (!groupTable.get(group).hasClient(client)) return "KO:You aren't in this group";
    groupTable.get(group).addMessage(new Message(client, subject, message));
    return "OK:Message sent successfully";
  }

  private String readMessages() {
    List<Message> result = clientTable.get(client).getUnreadMessages();
    if (result.isEmpty()) return "KO:No new messages";
    StringBuilder sb = new StringBuilder().append("OK");
    for (Message m : result) {
      sb.append(':').append(m.getMessage());
      m.read();
    }
    return sb.toString();
  }

  private String createGroup(Scanner data) {
    String groupName = data.next();
    if (groupTable.containsKey(groupName)) return "KO:Group name already exists";
    groupTable.put(groupName, new Group(groupName));
    return "OK:Group created";
  }

  private String joinGroup(Scanner data) {
    String groupName = data.next();
    if (!groupTable.containsKey(groupName)) return "KO:Group doesn't exists";
    groupTable.get(groupName).addClient(clientTable.get(client));
    return "OK:Joined the group successfully";
  }
}
