package Server;

import java.net.*;

public class VSMTPServer {
  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket = new ServerSocket(1234);
    System.out.println("Server Started!\nServer log:");
    while (true) new VSMTPThread(serverSocket.accept()).start();
  }
}
