package cw6;

import java.io.*;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class Alice {

  private static final int VERTS = 5;
  private static final int EDGES = VERTS*VERTS/4;

  public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {

    // 1. Alice zna graf G z cyklem Hamiltona H
    HGraph G = new HGraph(VERTS,EDGES);
    graph.generate();

      // Wizualizacja
      System.out.println("BASE_G_CYCLE: "+graph.getHCycle().toString());
      printGraph(graph.getGraphTable());

    // 2. Bob zna G bez cyklu
    //TODO wysłać G do Boba

    // 5 rund
    for (int i=0;i<5 ;i++ ) {

      // 3a. Alice generuje graf izomorficzny
      List<Integer> iso = graph.isomorphism();
        System.out.println("ISO: "+iso.toString());
      HGraph isoGraph = new HGraph(graph,iso);
        System.out.println("ISO_G_CYCLE: "+isoGraph.getHCycle().toString());
        printGraph(isoGraph.getGraphTable());

      // 3b. Alice wykonuje zobowiązanie bitowe dla M_G oraz numeracji wierszy i kolumn
      CommitedGraph cg = new CommitedGraph("SHA-256");
      cg.loadGraph(isoGraph,iso);
      cg.commit();
        printGraph(cg.getCommGraphTable());

      // 4. Wysyła zakryty graf do Boba
      // TODO wysłać zakryty

      // 5. Bob rzuca monetą
      // TODO read

        // 6. Ujawnia cykl Hamiltona w G'

        // 6. Ujawnia G' + numeracja w i k
    }
  }

  public static void printGraph(boolean[][] graph) {
    for (boolean[]  bT : graph ) {
      for (boolean b:bT) {
        if (b) System.out.print("1 ");
        else System.out.print("- ");
      }System.out.print('\n');
    }
  }
  public static void printGraph(byte[][][] graph) {
    for (byte[][]  bT : graph ) {
      for (byte[] b:bT) {
        for (byte by : b ) {
          System.out.printf("0x%02X ",by);
        }System.out.print(" | ");
      }System.out.print('\n');
    }
  }

}
