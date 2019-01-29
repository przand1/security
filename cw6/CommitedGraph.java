package cw6;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class CommitedGraph {

  private byte[][][] commGraphTable;  // M_ij = H(wartość,losowa liczba)
                                      // tablica 2D wartości typu byte[], zwracanego przez funkcję haszującą
  private List<byte[]> commIsomorph;  // H(izomorfizm,losowa liczba)

  private String hashAlgorithm; // np. SHA-256, stosowany w f. haszującej
  private MessageDigest md; // funkcja haszująca

  private SecureRandom rand;  // generator liczb losowych
  private byte[][] graphSeeds;  // losowe wartości, po jednej dla każdego M_ij
  private byte[] isoSeeds;

  private HGraph G; // graf bazowy, na którym będziemy pracować. Później można załadować kolejny.
  private List<Integer> isomorph; // izomorfizm z którego powstał graf bazowy

  // Alice wysyła zakryty graf do Boba
  public byte[][][] getCommGraphTable() {return commGraphTable;}

  // przypadek 1: ujawnij cykl Hamiltona:
  // public byte[][][] getHCHashes() {
  //
  // }

  //przypadek 2: ujawnij graf i izomorfizm:
  public List<byte[]> getCommIsomorph() {return commIsomorph;}
  public byte[][] getGraphSeeds() {return graphSeeds;}
  public byte[] getIsoSeeds() {return isoSeeds;}
  //



  // podstawowy konstruktor
  public CommitedGraph(String hashAlgorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    this.hashAlgorithm = hashAlgorithm;
    commIsomorph = new ArrayList<>();
    md = MessageDigest.getInstance(hashAlgorithm);
    rand = SecureRandom.getInstance("SHA1PRNG");
  }

  // wczytaj graf i jego izomorfizm
  public void loadGraph(HGraph G, List<Integer> isomorph) {
    this.G = G;
    this.isomorph = isomorph;
    int verts = G.getVerts();
    commGraphTable = new byte[verts][verts][];
    graphSeeds = new byte[verts][verts];
    for (byte[] row : graphSeeds ) {
      rand.nextBytes(row);
      for (byte b : row ) {
    }
    isoSeeds = new byte[verts];
    rand.nextBytes(isoSeeds);
  }

  // zakryj co trzeba
  /*
    potrzebujemy:
      * ujawnić graf → stworzyć zakryty graf, wysłać + pełna tab wart losowych
      * ujawnić izomorfizm → zakryć izo.
      * ujawnić tylko krawędzie składające się na cykl Hamiltona
        → 1. wysłać zakryty 2. wysłać częściowo zapełnioną tabelę wart. losowych
  */
  public void commit() throws UnsupportedEncodingException {
    boolean[][] gt = G.getGraphTable();
    int verts = G.getVerts();
    commitGraph(verts,gt);
    commitIsomorph();
  }

  private void commitIsomorph() {
    for (int i = 0;i < isomorph.size();i++) {
      md.update(BigInteger.valueOf(isomorph.get(i)).toByteArray()); // zmiana int na byte[], bo int nie wchodzi
      md.update(isoSeeds[i]);
      byte[] b = md.digest();
      commIsomorph.add(b);
    }
  }

  private void commitGraph(int verts,boolean[][] gt) {
    for (int i = 0;i < verts ;i++ ) {
      for (int j = 0;j < verts ;j++ ) {
        if (gt[i][j]) {
          md.update((byte)1);
        }
        else {
          md.update((byte)0);
        }
        md.update(graphSeeds[i][j]);
        commGraphTable[i][j] = md.digest();
      }
    }
  }

}
