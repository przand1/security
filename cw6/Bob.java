package cw6;

import java.net.*;
import java.io.*;

public class Bob {

  public static void main(String[] args) {
    if (args.length<2) {
      System.out.println("Server IP and port required.");
      return;
    }
    try {
      Socket clientSocket = new Socket(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());



      // 2. Odebrać G od Alice

      // 4. Odebrać zakryty G'

      // 5. Rzut monetą

        // 6. Odebrać cykl Hamiltona

        // 6. odebrać G' i numerację w,k

        // 7. Sprawdzić czy G'~ G



      clientSocket.close();

    } catch (ConnectException c) {
      System.out.println("Connection refused. Is server on?");
    } catch (FileNotFoundException f) {
      System.out.println("File not found. Check file paths in BOTH classes.");
    }
     catch(Exception e) {
      e.printStackTrace();
    }
  }

}
