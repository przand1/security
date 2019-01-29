package cw6;

import java.net.*;
import java.io.*;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class Alice {

  private static final int VERTS = 5;
  private static final int EDGES = VERTS*VERTS/4;

  public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, IOException, UnknownHostException {

    //utwórz połączenie
    ServerSocket serverSocket = new ServerSocket(9999);
    System.out.println(InetAddress.getByName("localhost"));
    Socket clientSocket = serverSocket.accept();

    System.out.println("Client connected: "+clientSocket);
    DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
    DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());

    GraphPrinter gp = new GraphPrinter();


    // 1. Alice zna graf G z cyklem Hamiltona H
    HGraph G = new HGraph(VERTS,EDGES);
    G.generate();

      // Wizualizacja
      System.out.println("BASE_G_CYCLE: "+G.getHCycle().toString());
      gp.printGraph(G.getGraphTable());

    // 2. Bob zna G bez cyklu
    clientOutput.writeInt(VERTS); // rozmiar grafus
    for (boolean[] B : G.getGraphTable() ) { // macierz grafu
      for (boolean BB : B ) {
        clientOutput.writeBoolean(BB);
      }
    }

    // 5 rund
    //for (int i=0;i<5 ;i++ ) {

      // 3a. Alice generuje graf izomorficzny
      List<Integer> iso = G.isomorphism();
        //System.out.println("ISO: "+iso.toString());
      HGraph isoGraph = new HGraph(G,iso);
        //System.out.println("ISO_G_CYCLE: "+isoGraph.getHCycle().toString());
        //gp.printGraph(isoGraph.getGraphTable());

      // 3b. Alice wykonuje zobowiązanie bitowe dla M_G oraz numeracji wierszy i kolumn
      CommitedGraph cg = new CommitedGraph("SHA-256");
      cg.loadGraph(isoGraph,iso);
      cg.commit();
        gp.printGraph(cg.getCommGraphTable());

      // 4. Wysyła zakryty graf do Boba
      for (byte[][] bb : cg.getCommGraphTable()) {
        for (byte[] b : bb) {
          // for (byte B:b) {
          //   clientOutput.write(B);
          // }
          clientSocket.getOutputStream().write(b);
        }
      }


      // 5. Bob rzuca monetą
      // TODO read

        // 6. Ujawnia cykl Hamiltona w G'

        // 6. Ujawnia G' + numeracja w i k


    //}

    serverSocket.close();
    clientSocket.close();
  }

}
