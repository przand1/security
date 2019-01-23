package cw6;

import java.security.MessageDigest;
import java.util.Random;

public static class CommitedGraph {

  private String[][] commGraphTable;
  private List<String> commIsomorph;
  private int verts;
  private String hashAlgorithm;
  private MessageDigest md;

  public CommitedGraph(int verts, String hashAlgorithm) {
    this.verts = verts;
    this.hashAlgorithm = hashAlgorithm;
    graphTable = new String[verts][verts];
    commIsomorph = new ArrayList<>();
    md = MessageDigest.getInstance(hashAlgorithm);
  }

  public void commit(HGraph G, List<int> isomorph) {
    boolean gt = G.getGraphTable();
    for (int i = 0;i < verts ;i++ ) {
      for (int j = 0;j<verts ;j++ ) {
        if (gt[i][j]) {
          md.update(1);
          byte[] b = md.digest();
          commGraphTable[i][j] = 
        }
      }
    }
  }

}
