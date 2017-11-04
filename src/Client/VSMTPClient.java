package Client;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class VSMTPClient {
  private static Scanner keyboard = new Scanner(System.in);

  private static int getOption(int options) {
    int option = -1;
    boolean valid = false;

    while (!valid) {
      try {
        option = Integer.parseInt(keyboard.nextLine());
        if (1 <= option && option <= options) valid = true;
        else System.out.println("Opcio no valida!");
      } catch (NumberFormatException e) {
        System.out.println("Opcio no valida!");
      }
    }

    return option;
  }

  public static void main(String[] args) throws Exception {
    Socket clientSocket = new Socket("localhost", 1234);
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
    outToServer.writeBytes("Hello World!");
    clientSocket.close();
  }
}
