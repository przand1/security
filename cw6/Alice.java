package cw6;

import java.net.*;
import java.io.*;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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
      System.out.println("\n### Graf podstawowy ###");
      gp.printGraph(G.getGraphTable());
      System.out.println("\n### Cykl Hamiltona ###\n"+G.getHCycle().toString());

    // 2. Bob zna G bez cyklu
    clientOutput.writeInt(VERTS); // rozmiar grafus
    for (boolean[] B : G.getGraphTable() ) { // macierz grafu
      for (boolean BB : B ) {
        clientOutput.writeBoolean(BB);
      }
    }
    System.out.println("\nGraf wysłany.");

    // 5 rund
    for (int ii=0;ii<5 ;ii++ ) {
        System.out.println("--------------------------------------- Runda "+ii+"---------------------------------------");
        // 3a. Alice generuje graf izomorficzny
        List<Integer> iso = G.isomorphism();
          System.out.println("\n### Izomorfizm ###\n"+iso.toString());
        HGraph isoGraph = new HGraph(G,iso);
          System.out.println("\n### Graf izomorficzny ###");
          gp.printGraph(isoGraph.getGraphTable());
          System.out.println("### Cykl Hamiltona ###\n"+isoGraph.getHCycle().toString());

        // 3b. Alice wykonuje zobowiązanie bitowe dla M_G oraz numeracji wierszy i kolumn
        CommitedGraph cg = new CommitedGraph("SHA-256"); // macierz grafu
        cg.loadGraph(isoGraph,iso);
        cg.commit();
          System.out.println("\n### Tabela wartości losowych ###");
          gp.printVals(cg.getGraphSeeds());
          System.out.println("\n### Wartości losowe numeracji ###");
          System.out.println(Arrays.toString(cg.getIsoSeeds()));
          // System.out.println("\n### Graf zakryty ###");
          // gp.printGraph(cg.getCommGraphTable());
          // System.out.println("\n### Numeracja zakryta ###");
          // cg.getCommIsomorph().forEach(v -> {
          //     for (byte by : v ) {
          //       System.out.printf("0x%02X ",by);
          //     }System.out.print(" | ");
          // });
          // System.out.println();

        // 4. Wysyła zakryty graf do Boba
        for (byte[][] BB : cg.getCommGraphTable() ) {
          for (byte[] B : BB ) {
            clientOutput.writeInt(B.length);
            clientOutput.write(B,0,B.length);
          }
        }
        System.out.println("\nZakryty graf wysłany.");
        //  Wysyłanie numeracji
        cg.getCommIsomorph().forEach(v -> {
          try {
            clientOutput.writeInt(v.length);
            clientOutput.write(v);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
        System.out.println("\nZakryta numeracja wysłana.");


        // 5. Bob rzuca monetą
        if (clientInput.readByte() == 1) {
          System.out.println("Ujawnić cykl Hamiltona");
          // 6. Ujawnia cykl Hamiltona w G' - tablica, wart. losowe, f. haszująca
          System.out.println("Wysyłanie cyklu...");
          for (boolean[] B : cg.getPureHamilton()) {
            for (boolean BB : B ) {
              clientOutput.writeBoolean(BB);
            }
          }
          System.out.println("Wysyłanie wartości losowych...");
          for (byte[] B : cg.getHCSeeds()) {
            clientOutput.write(B,0,B.length);
          }
          System.out.println("Wysyłanie funkcji haszującej...");
          clientOutput.writeUTF("SHA-256");

        }
        else {
          // 6. Ujawnia G' + numeracja w i k // macierz grafu
          System.out.println("\nUjawnić G' + numeracja w i k");
          for (boolean[] B : isoGraph.getGraphTable() ) {          // G'
            for (boolean BB : B ) {
              clientOutput.writeBoolean(BB);
            }
          }
          iso.forEach(v -> {                                       // izomorfizm - numeracja
            try{
              clientOutput.writeInt(v);
            } catch(IOException ioE) {
              ioE.printStackTrace();
            }
          });
          for ( byte[] B : cg.getGraphSeeds() ) { // randVals
            clientOutput.write(B,0,B.length);
          }
          clientOutput.write(cg.getIsoSeeds());  // isoRands
          clientOutput.writeUTF("SHA-256"); // hash func.

        }
      }

    serverSocket.close();
    clientSocket.close();
  }

}
