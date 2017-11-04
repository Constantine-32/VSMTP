package Server;

import java.net.*;
import java.util.Scanner;

public class VSMTPServer {
  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket = new ServerSocket(1234);

    while (true) {
      Socket socket = serverSocket.accept();
      Scanner inFromClient = new Scanner(socket.getInputStream());
      while (inFromClient.hasNext()) {
        System.out.println(inFromClient.nextLine());
      }
    }
  }
}
