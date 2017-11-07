package Server;

import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class VSMTPServer {


  public static void main(String[] args) throws Exception {
    ServerSocket serverSocket = new ServerSocket(1234);

    while (true) {
      new VSMTPThread(serverSocket.accept()).start();
    }
  }
}
