package cw6;

import java.io.*;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class Alice {

  private static final int VERTS = 5;
  private static final int EDGES = VERTS*VERTS/4;

  public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    HGraph graph = new HGraph(VERTS,EDGES);
    graph.generate();
    System.out.println("BASE_G_CYCLE: "+graph.getHCycle().toString());
    printGraph(graph.getGraphTable());
    List<Integer> iso = graph.isomorphism();
    System.out.println("ISO: "+iso.toString());
    HGraph isoGraph = new HGraph(graph,iso);
    System.out.println("ISO_G_CYCLE: "+isoGraph.getHCycle().toString());
    printGraph(isoGraph.getGraphTable());

    CommitedGraph cg = new CommitedGraph("SHA-256");
    cg.loadGraph(isoGraph,iso);
    cg.commit();
    printGraph(cg.getCommGraphTable());


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
