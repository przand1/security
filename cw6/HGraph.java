package cw6;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.io.*;

public class HGraph {

  private boolean[][] graphTable;
  private List<Integer> hCycle;
  private Random random;
  private int verts;

  public List getHcycle() { return hCycle; }
  public boolean[][] getGraphTable() { return graphTable; }

  public HGraph(int verts) {
    this.verts = verts;
    graphTable = new boolean[verts][verts];
    hCycle = new ArrayList<>();
    random = new Random();
  }

  public void generate() {
    //tworzenie losowego cyklu Hamiltona
    for (int i = 0;i < verts ; i++ ) {
      hCycle.add(i);
    }
    Collections.shuffle(hCycle);
    //zapis
    for (int i = 0;i < hCycle.size()-1 ;) {
      graphTable[hCycle.get(i)][hCycle.get(i++)] = true;
      System.out.println("Inserted: "+hCycle.get(i-1)+" "+hCycle.get(i));
    }

    graphTable[4][6] = true;
  }


}
