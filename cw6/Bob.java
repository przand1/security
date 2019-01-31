package cw6;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class Bob {

  public static void main(String[] args) {
    if (args.length<2) {
      System.out.println("Server IP and port required.");
      return;
    }
    try {
      Socket clientSocket = new Socket(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());

      GraphPrinter gp = new GraphPrinter();
      Scanner sc = new Scanner(System.in);

      // 2. Odebrać G od Alice
      int verts = clientInput.readInt();
      boolean[][] G = new boolean[verts][verts];
      for (int i=0;i<verts;i++) {
        for (int j=0;j<verts;j++) {
          G[i][j] = clientInput.readBoolean();
        }
      }
      System.out.println("\n### Otrzymano graf ###");
      gp.printGraph(G);
      for(int ii =0;ii<5;ii++) {
        System.out.println("--------------------------------------- Runda "+ii+"---------------------------------------");
        // 4. Odebrać zakryty G'
        byte[][][] commG1 = new byte[verts][verts][];
        for(int i = 0;i < verts; i++) {
          for (int j = 0; j < verts ; j++ ) {
            commG1[i][j] = new byte[clientInput.readInt()];
            clientInput.read(commG1[i][j]);
          }
        }
        System.out.println("\n### Otrzymano zakryty graf ###");
        // gp.printGraph(commG1);
        List<byte[]> commG1Iso = new ArrayList<>();
        for(int i = 0;i < verts; i++) {
          byte[] temp = new byte[clientInput.readInt()];
          clientInput.read(temp);
          commG1Iso.add(temp);
        }
        System.out.println("\n### Otrzymano zakrytą numerację ###");
        // commG1Iso.forEach(v -> {
        //     for (byte by : v ) {
        //       System.out.printf("0x%02X ",by);
        //     }System.out.print(" | ");
        // });
        // System.out.println();

        // 5. Rzut monetą
        if (sc.nextInt() == 1) {
          System.out.println("Wybrano ujawnienie cyklu Hamiltona");
          clientOutput.write(1);
          // 6. Odebrać cykl Hamiltona
          boolean[][] PH = new boolean[verts][verts];
          byte[][] PHseeds = new byte[verts][verts];
          for (int i=0;i<verts;i++) {
            for (int j=0;j<verts;j++) {
              PH[i][j] = clientInput.readBoolean();
            }
          }
          System.out.print("\nOtrzymano cykl\n");
          gp.printGraph(PH);
          for (int i=0;i<verts;i++) {
            clientInput.read(PHseeds[i]);
          }
          System.out.println("Sprawdzanie czy jest cyklem Hamiltona...");
          if(isHamilton(PH)) System.out.println("OK");
          else {
            System.out.println("FAIL");
            System.out.println("Zrywam połączenie");
            clientSocket.close();

          }
          System.out.println("\nOtrzymano wart. losowe\n");
          gp.printVals(PHseeds);
          String algo = clientInput.readUTF();
          System.out.println("\nOtrzymano f. haszującą\n");
          System.out.println(algo);

          System.out.println("\n\n############ SPRAWDZANIE ############\n");

          System.out.println("\nHaszowanie otrzymanego cyklu...\n");
          byte[][][] commitedPH = new byte[verts][verts][];
          MessageDigest md = MessageDigest.getInstance(algo);
          for (int i=0;i<verts;i++) {
            for (int j=0;j<verts;j++) {
              if (PH[i][j]) {
                md.update((byte)1);
              } else md.update((byte)0);
              md.update(PHseeds[i][j]);
              commitedPH[i][j] = md.digest();
            }
          }
          System.out.println("\nPorównywanie...\n");
          boolean ok = true;
          out:
          for (int i = 0; i < verts; i++ ) {
            for (int j = 0;j<verts ;j++ ) {
              if (PH[i][j]) {
                for (int k = 0;k < commitedPH[i][j].length ;k++ ) {
                  if (commitedPH[i][j][k] != commG1[i][j][k]) {
                    ok = false;
                    break out;
                  }
                }

              }
            }
          }
          if (ok) {
            System.out.println("OK");
          }
          else {
            System.out.println("FAIL");
            System.out.println("Zrywam połączenie");
            clientSocket.close();
          }

        } else {
          System.out.println("Wybrano sprawdzenie izomorfizmu");
          clientOutput.write(0);
          // 6. odebrać G' i numerację w,k
           // G'
          boolean[][] G1 = new boolean[verts][verts];
          for (int i=0;i<verts;i++) {
            for (int j=0;j<verts;j++) {
              G1[i][j] = clientInput.readBoolean();
            }
          }
          System.out.println("\n### Otrzymano graf izomorficzny ###");
          gp.printGraph(G1);
          // numeracja
          List<Integer> iso = new ArrayList<>();
          for (int i = 0 ; i < verts ; i++ ) {
            iso.add(clientInput.readInt());
          }
          System.out.println("\n### Otrzymano numerację ###");
          System.out.println(iso);
          //  randVals
          byte[][] randVals = new byte[verts][verts];
          for (int i = 0;i<verts;i++) {
            clientInput.read(randVals[i]);
          }
          System.out.println("\n### Otrzymano wart. losowe ###");
          gp.printVals(randVals);
          //  isoRands
          byte[] isoRands = new byte[verts];
          clientInput.read(isoRands);
          System.out.println("\n### Otrzymano wart. losowe numeracji ###");
          System.out.println(Arrays.toString(isoRands));
          //  hash func.
          String hashFunc = clientInput.readUTF();
          System.out.println("\n### Otrzymano f. haszującą ###");
          System.out.println(hashFunc);
          // 7. Sprawdzić czy G'~ G

          System.out.println("\n\n################# SPRAWDZANIE #################");

          byte[][][] newCommitedG = commitGraph(verts,G1,randVals,hashFunc);
          System.out.println("Haszowanie...");
          boolean ok = true;
          out:
          for (int i = 0; i < verts; i++ ) {
            for (int j = 0;j<verts ;j++ ) {
              for (int k = 0;k < commG1[i][j].length ;k++ ) {
                if (commG1[i][j][k] != newCommitedG[i][j][k]) {
                  ok = false;
                  break out;
                }
              }
            }
          }
          if (ok) {
            System.out.println("Zakryty graf prawidłowy.");
          }
          else {
            System.out.println("Zakryty graf nieprawidłowy.");
            System.out.println("Zrywam połączenie");
            clientSocket.close();
          }

          System.out.println("Haszowanie...");
          List<byte[]> newCommitedIso = commitIsomorph(isoRands,iso,hashFunc);
          ok = true;
          out2:
          for (int i = 0;i < commG1Iso.size() ; i++ ) {
            byte[] b1 = commG1Iso.get(i);
            byte[] b2 = newCommitedIso.get(i);
            for (int j = 0;j < b1.length ; j++ ) {
              if (b1[j] != b2[j]) {
                ok = false;
                break out2;
              }
            }
          }
          if (ok) {
            System.out.println("Numeracja prawidłowa.");
          }
          else {
            System.out.println("Numeracja nieprawidłowa.");
            System.out.println("Zrywam połączenie");
            clientSocket.close();
          }

          System.out.println("Sprawdzanie izomorfizmu...");
          boolean[][] newGT = new boolean[verts][verts];
          ok = true;
          for (int i = 0; i < verts ; i++ ) { // for each odpada, potrzebne numery indeksów
            for (int j = 0;j < verts ; j++ ) {
              newGT[iso.get(i)][iso.get(j)] = G[i][j];
            }
          }
          gp.printGraph(newGT);
          System.out.println();
          gp.printGraph(G1);
          for (int i = 0; i < verts ; i++ ) { // for each odpada, potrzebne numery indeksów
            for (int j = 0;j < verts ; j++ ) {
              if (newGT[i][j] != G1[i][j]) {
                ok = false;
              }
            }
          }
          if (ok) {
            System.out.println("Graf jest izomorficzny.");
          }
          else {
            System.out.println("Graf nie jest izomorficzny.");
            System.out.println("Zrywam połączenie");
            clientSocket.close();
          }

        }
      }

      clientSocket.close();

    } catch (ConnectException c) {
      System.out.println("Connection refused. Is server on?");
    } catch (FileNotFoundException f) {
      System.out.println("File not found. Check file paths in BOTH classes.");
    }
     catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static byte[][][] commitGraph(int verts,boolean[][] gt,byte[][] graphSeeds,String algo) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance(algo);
    byte[][][] commGraphTable = new byte[verts][verts][];
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
    return commGraphTable;
  }
  private static List<byte[]> commitIsomorph(byte[] isoSeeds,List<Integer> isomorph,String algo) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance(algo);
    List<byte[]> commIsomorph = new ArrayList<>();
    for (int i = 0;i < isomorph.size();i++) {
      md.update(BigInteger.valueOf(isomorph.get(i)).toByteArray()); // zmiana int na byte[], bo int nie wchodzi
      md.update(isoSeeds[i]);
      byte[] b = md.digest();
      commIsomorph.add(b);
    }
    return commIsomorph;
  }

  private static boolean isHamilton(boolean[][] tab) {
    int beginRow;
    int next = -1;
    int verts = tab.length;
    List<Integer> cycle = new ArrayList<>();
    findFirst:
    for (int i = 0;i < verts ;i++ ) {
      for (int j = 0;j < verts ;j++ ) {
        if (tab[i][j]) {
          beginRow = i;
          next = j;
          cycle.add(i);
          cycle.add(j);
          System.out.println(beginRow+" "+next+" "+cycle);
          break findFirst;
        }
      }
    }
    if(next < 0) return false;
    while(cycle.size() < verts) {
      findNext:
      for (int j = 0;j < verts ;j++ ) {
        if (tab[next][j] && !cycle.contains(j)) {
          cycle.add(j);
          next = j;
          break findNext;
        }
      }
      System.out.println(cycle);
    }
    return tab[cycle.get(cycle.size()-1)][cycle.get(0)];
  }

}
