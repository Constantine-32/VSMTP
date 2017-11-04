package Server;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class VSMTPThread extends Thread {
  private static int nextId = 1;
  private int threadId;
  private Socket socket;

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
        outToClient.writeBytes("OK:Default OK message\n");
      }
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    System.out.println("Thread["+threadId+"] CLOSED");
  }
}
