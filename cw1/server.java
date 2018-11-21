package cw1;

import java.net.*;
import java.io.*;

public class server {

  public static void main(String[] args) {
    try{
      ServerSocket serverSocket = new ServerSocket(9999);
      System.out.println(InetAddress.getByName("localhost"));
      Socket clientSocket = serverSocket.accept();

      System.out.println("Client connected: "+clientSocket);
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());

      String toEncrypt = "nic ciekawego";
      String key = "qwertyuiop";
      System.out.println("Przed: "+toEncrypt);
      toEncrypt=aes.encrypt(toEncrypt,key);
      System.out.println("Po: "+toEncrypt);
      System.out.println("Wysyłam");

      clientOutput.writeUTF(toEncrypt);

      serverSocket.close();
      clientSocket.close();
    }catch(Exception e){
      e.printStackTrace();
    }

  }
}
