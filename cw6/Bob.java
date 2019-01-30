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
      // 4. Odebrać zakryty G'
      byte[][][] commG1 = new byte[verts][verts][];
      for(int i = 0;i < verts; i++) {
        for (int j = 0; j < verts ; j++ ) {
          commG1[i][j] = new byte[clientInput.readInt()];
          clientInput.read(commG1[i][j]);
        }
      }
      System.out.println("\n### Otrzymano zakryty graf ###");
      gp.printGraph(commG1);
      List<byte[]> commG1Iso = new ArrayList<>();
      for(int i = 0;i < verts; i++) {
        byte[] temp = new byte[clientInput.readInt()];
        clientInput.read(temp);
        commG1Iso.add(temp);
      }
      System.out.println("\n### Otrzymano zakrytą numerację ###");
      commG1Iso.forEach(v -> {
          for (byte by : v ) {
            System.out.printf("0x%02X ",by);
          }System.out.print(" | ");
      });
      System.out.println();

      // 5. Rzut monetą
      if (sc.nextInt() == 1) {
        clientOutput.write(1);
        // 6. Odebrać cykl Hamiltona
        //TODO

      } else {
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
        else System.out.println("Zakryty graf nieprawidłowy.");

        System.out.println("Haszowanie...");
        List<byte[]> newCommitedIso = commitIsomorph(isoRands,iso,hashFunc);
        ok = true;
        out2:
        for (byte[] b : commG1Iso ) {
          for (int i = 0;i < b.length ; i++ ) {
            if (b[i] != newCommitedIso.get(commG1Iso.indexOf(b))[i]) {  // TODO TU MUSI BYĆ BŁĄD !!!!
              ok = false;
              break out2;
            }
          }
        }
        if (ok) {
          System.out.println("Numeracja prawidłowa.");
        }
        else System.out.println("Numeracja nieprawidłowa.");

        System.out.println("Sprawdzanie izomorfizmu...");
        boolean[][] newGT = new boolean[verts][verts];
        ok = true;
        out3:
        for (int i = 0; i < verts ; i++ ) { // for each odpada, potrzebne numery indeksów
          for (int j = 0;j < verts ; j++ ) {
            //newGT[i][j] = G[iso.get(i)][iso.get(j)];
            if (G1[i][j] != G[iso.get(i)][iso.get(j)]) {
              ok = false;
              break out3;
            }
          }
        }
        //gp.printGraph(newGT);
        if (ok) {
          System.out.println("Graf jest izomorficzny.");
        }
        else System.out.println("Graf nie jest izomorficzny.");


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

}
