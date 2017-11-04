package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class VSMTPThread extends Thread {
  private static int nextId = 1;
  private int threadId;
  private Socket socket;

  public VSMTPThread(Socket socket) {
    this.socket = socket;
    threadId = nextId++;
    System.out.println("Thread["+threadId+"] STARTED");
  }

  @Override
  public void run() {
    try {
      Scanner inFromClient = new Scanner(socket.getInputStream());
      DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
      while (inFromClient.hasNext()) {
        System.out.println("Thread["+threadId+"] "+inFromClient.nextLine());
        outToClient.writeBytes("OK:Default OK message\n");
      }
    } catch (IOException e) {
      System.out.println(e.toString());
    }
    System.out.println("Thread["+threadId+"] CLOSED");
  }
}
