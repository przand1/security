package cw6;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class HGraph {

  private boolean[][] graphTable; // M_ij = true -> istnieje krawędź między v_i i v_j
  private List<Integer> hCycle; // cykl Hamiltona jako lista numerów wierzchołków
  private Random random;
  private int verts; // liczba wierzchołków
  private int edgeInserts; // liczba prób wstawienia dodatkowych krawędzi
  private boolean[][] graphTableHCOnly;

  public List<Integer> getHCycle() { return hCycle; }
  public boolean[][] getGraphTable() { return graphTable; }
  public int getVerts() { return verts; }
  public int getEdgeInserts() { return edgeInserts; }

  public void setGraphTable(boolean[][] graphTable) {
    this.graphTable = graphTable;
  }
  private void setGraphTableHCOnly() {
    graphTableHCOnly = new boolean[verts][verts];
    for (int i = 0;i < hCycle.size()-1 ;i++) {
      graphTable[hCycle.get(i)][hCycle.get(i+1)] = true;
      graphTable[hCycle.get(i+1)][hCycle.get(i)] = true;
    }
  }
  public boolean[][] getGraphTableHCOnly() {return graphTableHCOnly;}

// konstruktor nowego grafu
  public HGraph(int verts,int edgeInserts) {
    this.verts = verts;
    this.edgeInserts = edgeInserts;
    graphTable = new boolean[verts][verts];
    hCycle = new ArrayList<>();
    random = new Random();
  }

// konstruktor grafu izomorficznego
// w main:
// HGraph g = new HGraph(10,20);
// g.generate();
// List iso = g.isomorphism();
// HGraph h = new HGraph(g,iso);
  public HGraph(HGraph base, List<Integer> isomorph) {
    this(base.getVerts(),base.getEdgeInserts());
    // teraz trzeba przepisać graphTable uwzględniając izomorfizm. Co z cyklem???
    boolean[][] tempGT = base.getGraphTable();
    for (int i = 0; i < verts ; i++ ) { // for each odpada, potrzebne numery indeksów
      for (int j = 0;j < verts ; j++ ) {
        graphTable[i][j] = tempGT[isomorph.get(i)][isomorph.get(j)];
      }
    }
    // teraz jeszcze cykl
    List<Integer> hc = base.getHCycle();
    for (int i : hc) {
      hCycle.add(isomorph.get(i));
    }
    // na później
    setGraphTableHCOnly();
  }

  public void generate() {
    // tworzenie losowej ścieżki Hamiltona
    for (int i = 0;i < verts ; i++ ) {
      hCycle.add(i);
    }
    Collections.shuffle(hCycle);
    // dodanie do grafu
    for (int i = 0;i < hCycle.size()-1 ;i++) {
      graphTable[hCycle.get(i)][hCycle.get(i+1)] = true;
      graphTable[hCycle.get(i+1)][hCycle.get(i)] = true;
    }
    // dopełnienie cyklu - krawędź między pierwszym i ostatnim wierzchołkiem
    graphTable[hCycle.get(0)][hCycle.get(verts-1)] = true;
    graphTable[hCycle.get(verts-1)][hCycle.get(0)] = true;
    // dodanie dodatkowych krawędzi
    for (int i = 0;i < edgeInserts ;i++) {
      int r1 = random.nextInt(verts);
      int r2 = random.nextInt(verts);
      graphTable[r1][r2] = graphTable[r2][r1] = true;
    }
  }

  //stwórz izomorfizm
  public List<Integer> isomorphism() {
    // wierzchołek nr 'indeks' przechodzi na wierzchołek nr 'wartość'
    List<Integer> izo = new ArrayList<>(hCycle);// Bo zawiera liczby od 0 do verts. I tak będzie permutowana.
    Collections.shuffle(izo); // nowa permutacja numerów wierzchołków
    return izo; // TODO dopisać konstruktor wykorzystujący to i gotowy graf.
  }
}
