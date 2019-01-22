package cw6;

import java.io.*;

public class Alice {

  private static final int VERTS = 30;
  private static final int EDGES = 20;

  public static void main(String[] args) {
    HGraph graph = new HGraph(VERTS);
    graph.generate();
    System.out.println(graph.getHcycle().toString());
    boolean[][] l = graph.getGraphTable();
    for (boolean[]  bT : l ) {
      for (boolean b:bT) {
        if (b) {
          System.out.print("1 ");
        }
        else System.out.print("0 ");
      }System.out.print('\n');
    }
  }

}
