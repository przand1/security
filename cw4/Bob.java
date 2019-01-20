package cw4;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;

public class Bob {

  private static final int NUM_OF_BITS = 500;

  public static void main(String[] args) {
    if (args.length<2) {
      System.out.println("Server IP and port required.");
      return;
    }
    try {
      Socket clientSocket = new Socket(InetAddress.getByName(args[0]),Integer.parseInt(args[1]));
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
      SecureRandom rand = new SecureRandom();

      // Bob odbiera p i g od Alice
      BigInteger prime = new BigInteger(clientInput.readUTF());
      BigInteger g = new BigInteger(clientInput.readUTF());

      // Bob odbiera Ya od Alice
      BigInteger Ya = new BigInteger(clientInput.readUTF());


//===========================================================
//WERYFIKACJA
//odczytanie klucza
System.out.println("Odczyt klucza...");
      FileInputStream aliceKeyFIS = new FileInputStream("/home/q/security/cw4/AlicePublicKey");
      byte[] encAliceKey = new byte[aliceKeyFIS.available()];
      aliceKeyFIS.read(encAliceKey);
      aliceKeyFIS.close();
//konwersja
System.out.println("Konwesja...");
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encAliceKey);
      KeyFactory keyFactory = KeyFactory.getInstance("DSA","SUN");
      PublicKey aliceKey = keyFactory.generatePublic(pubKeySpec);
//odczytanie podpisu
System.out.println("Odczyt podpisu...");
      FileInputStream sigFIS = new FileInputStream("/home/q/security/cw4/AliceSignature");
      byte[] sigToVerify = new byte[sigFIS.available()];
      sigFIS.read(sigToVerify);
      sigFIS.close();
//init funkcji ver
      Signature signature = Signature.getInstance("SHA1withDSA","SUN");//algorytm podpisu/weryfikacji
      signature.initVerify(aliceKey);
//przygotowanie danych do weryfikacji
      signature.update(prime.toByteArray());
      signature.update(g.toByteArray());
      signature.update(Ya.toByteArray());
//weryfikacja podpisu
System.out.print("Weryfikacja... ");
      boolean verified = signature.verify(sigToVerify);
//jeśli OK przejdź do komunikacji
//===========================================================================


      if (verified) {
        System.out.print("OK\n");

        // Bob losuje 1 <= Xb < p
        BigInteger Xb = new BigInteger(rand.nextInt(NUM_OF_BITS),rand).mod(prime);

        // Bob oblicza Yb = g^Xb(mod p)
        BigInteger Yb = g.modPow(Xb,prime);

        // Bob wysyła Yb do Alice
        clientOutput.writeUTF(Yb.toString());

        // Bob oblicza s = Ya^Xb(mod p)
        BigInteger s = Ya.modPow(Xb,prime);

        System.out.println("\nJak na razie:\n"+
          "prime = "+prime+'\n'+
          "g = "+g+'\n'+
          "Xb = "+Xb+'\n'+
          "Yb = "+Yb+'\n'+
          "Ya = "+Ya+'\n'+
          "s = "+s+'\n'
          );

        // Komunikacja przez AES
          String msg = clientInput.readUTF();
          System.out.println("Otrzymano: "+msg);
          System.out.println("Po odszyfrowaniu: "+cw1.aes.decrypt(msg,s.toString()));

          msg = "Wiadomość do Alice.";
          System.out.println("Wiadomość: "+msg);
          msg = cw1.aes.encrypt(msg,s.toString());
          System.out.println("Po zaszfrowaniu: "+msg);
          System.out.println("Wysyłanie...");

          clientOutput.writeUTF(msg);
        } else System.out.print("FAIL\n");

      clientSocket.close();
    } catch (ConnectException c) {
      System.out.println("Connection refused. Is server on?");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
