package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class VSMTPThread extends Thread {
  private static int nextId = 1;
  private int threadId;
  private Socket socket;
  private static Map<String, Client> userTable = new HashMap<>();
  private String thisClient;

  VSMTPThread(Socket socket) {
    this.socket = socket;
    threadId = nextId++;
    System.out.println("Thread["+threadId+"] STARTED");
  }

  @Override
  public synchronized void run() {
    try {
      Scanner inFromClient = new Scanner(socket.getInputStream());
      DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
      while (inFromClient.hasNext()) {
        String clientData = inFromClient.nextLine();

        // Client Data Processing
        System.out.println("Thread["+threadId+"] "+clientData);
        outToClient.writeBytes(processRequest(clientData)+"\n");
      }
      socket.close();
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    System.out.println("Thread["+threadId+"] CLOSED");
  }

  private String processRequest(String clientData) {
    Character code = clientData.charAt(0);
    String response="";
    switch (code) {
      case 'R':
        response= register(clientData);
        break;
      case 'D':
        response=  unregister(clientData);
        break;
      case 'E':
        response=  send(clientData);
        break;
      case 'G':
        //createGroup(clientData);
        break;
      case 'M':
        response= readMessages();
        break;
      case 'J':
        //addToGroup(clientData);
        break;
    }
    return response;
  }

  private String register(String clientData){
     String name = clientData.substring(2, clientData.length());
     thisClient=name;
     userTable.put(name, new Client(name));
     System.out.println(userTable);
     return "registered";
  }

  private String unregister(String clientData){
    userTable.get(thisClient).setRegistered(false);
    return "unregistered";
  }

  private String send(String clientData){
    Scanner data = new Scanner(clientData).useDelimiter(":");
    String line = data.next();
    String to = data.next();
    String subject = data.next();
    String message = data.next();
    userTable.get(to).addMessage(new Message(thisClient, subject, message));
    System.out.println(to);
    return "message sent "+userTable.get(to).toString()+"\n";
  }

  private String readMessages(){
    return (userTable.get(thisClient).getUnreadMessages()).toString();
  }





}
