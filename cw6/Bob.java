package cw6;

import java.net.*;
import java.io.*;
import java.util.Arrays;

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

      GraphPrinter gp = new GraphPrinter();

      // 2. Odebrać G od Alice
      int verts = clientInput.readInt();
      boolean[][] G = new boolean[verts][verts];
      for (int i=0;i<verts;i++) {
        for (int j=0;j<verts;j++) {
          G[i][j] = clientInput.readBoolean();
        }
      }
      gp.printGraph(G);
      // 4. Odebrać zakryty G'
      byte[] buffer = new byte[256];
      int len;
      byte[][][] G1 = new byte[verts][verts][100];
      for (int i=0;i<verts;i++) {
        for (int j=0;j<verts;j++) {
          // len = clientInput.read(buffer);
          // System.out.println(len);
          // G1[i][j] = Arrays.copyOf(buffer,len);
          clientInput.read(G1[i][j]);
        }
      }
      gp.printGraph(G1);

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
