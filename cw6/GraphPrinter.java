package cw6;

import java.io.*;

public class GraphPrinter {

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
  public static void printVals(byte[][] vals) {
    for (byte[] b:vals) {
      for (byte by : b ) {
        System.out.printf("%d ",by);
      }System.out.print("\n");
    }
  }

}
