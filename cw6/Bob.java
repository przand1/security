package cw6;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

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
      Scanner sc = new Scanner(System.in);

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
      byte[][][] commG1 = new byte[verts][verts][];
      for(int i = 0;i < verts; i++) {
        for (int j = 0; j < verts ; j++ ) {
          commG1[i][j] = new byte[clientInput.readInt()];
          clientInput.read(commG1[i][j]);
        }
      }
      gp.printGraph(commG1);


      // 5. Rzut monetą
      if (sc.nextInt() == 1) {
        clientOutput.write(1);
        // 6. Odebrać cykl Hamiltona
        //TODO

      } else {
        clientOutput.write(0);
        // 6. odebrać G' i numerację w,k
        boolean[][] G1 = new boolean[verts][verts];
        for (int i=0;i<verts;i++) {
          for (int j=0;j<verts;j++) {
            G1[i][j] = clientInput.readBoolean();
          }
        }
        gp.printGraph(G1);
        List<Integer> iso = new ArrayList<>();
        for (int i = 0 ; i < verts ; i++ ) {
          iso.add(clientInput.readInt());
        }
        System.out.println(iso);
        // 7. Sprawdzić czy G'~ G
        //TODO

      }

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
