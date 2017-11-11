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
  private Client client;

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
    case "I": return singIn(data);
    case "R": return signUp(data);
    case "S": return sendToClient(data);
    case "T": return sendToGroup(data);
    case "M": return readMessages();
    case "G": return createGroup(data);
    case "J": return joinGroup(data);
    case "L": return leaveGroup(data);
    case "D": return deleteAccount();
    default: return "KO:Unknown code";
    }
  }

  private String singIn(Scanner data) {
    String clientName = data.next();
    client = clientTable.get(clientName);
    if (client == null || !client.isActive()) return "KO:User doesn't exists";
    return "OK:Logged in successfully";
  }

  private String signUp(Scanner data) {
    String clientName = data.next();
    client = clientTable.get(clientName);
    if (client == null) {
      client = new Client(clientName);
      clientTable.put(clientName, client);
      return "OK:Registered successfully";
    }
    if (!client.isActive()) {
      client.setActive(true);
      return "OK:Registered successfully";
    }
    return "KO:Username already exists";
  }

  private String sendToClient(Scanner data) {
    String recipient = data.next();
    String subject = data.next();
    String message = data.next();
    if (clientTable.containsKey(recipient)) {
      clientTable.get(recipient).addMessage(new Message(client.getName(), subject, message));
      return "OK:Message sent successfully";
    } else return "KO:Message NOT sent, unknown recipient";
  }

  private String sendToGroup(Scanner data) {
    String group = data.next();
    String subject = data.next();
    String message = data.next();
    if (!groupTable.containsKey(group)) return "KO:Group doesn't exists";
    if (!groupTable.get(group).hasClient(client.getName())) return "KO:You aren't in this group";
    groupTable.get(group).addMessage(new Message(client.getName(), subject, message));
    return "OK:Message sent successfully";
  }

  private String readMessages() {
    List<Message> result = client.getUnreadMessages();
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
    groupTable.get(groupName).addClient(client);
    return "OK:Joined the group successfully";
  }

  private String leaveGroup(Scanner data) {
    String groupName = data.next();
    if (!groupTable.containsKey(groupName)) return "KO:Group doesn't exists";
    if (!groupTable.get(groupName).hasClient(client.getName())) return "KO:You aren't in that group";
    groupTable.get(groupName).removeClient(client);
    return "OK:Leaved the group successfully";
  }

  private String deleteAccount() {
    client.setActive(false);
    return "OK:Unregistered successfully";
  }
}
