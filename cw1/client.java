package cw1;

import java.net.*;
import java.io.*;

public class client {
  public static void main(String[] args) {
    try {
      Socket clientSocket = new Socket(InetAddress.getByName("150.254.79.90"),1764);
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
      String toDecrypt = clientInput.readUTF();
      System.out.println("From Server: "+toDecrypt);
      toDecrypt=aes.decrypt(toDecrypt,"qwertyuiop");
      System.out.println("Po: "+toDecrypt);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
