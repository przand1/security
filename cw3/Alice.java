package cw3;

import java.net.*;
import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class Alice {

  private static final int NUM_OF_BITS = 500;

  public static void main(String[] args) {
    try{
      ServerSocket serverSocket = new ServerSocket(9999);
      System.out.println(InetAddress.getByName("localhost"));
      Socket clientSocket = serverSocket.accept();

      System.out.println("Client connected: "+clientSocket);
      DataInputStream clientInput = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());

      // 1. Alice generuje p i g.
      SecureRandom rand = new SecureRandom();
      BigInteger q;
      BigInteger prime;
      BigInteger g;

      do {
         q = BigInteger.probablePrime(NUM_OF_BITS,rand);
         prime = q.multiply(new BigInteger("2")).add(BigInteger.ONE);
      } while (!prime.isProbablePrime(1));

      do {
        g = new BigInteger(rand.nextInt(NUM_OF_BITS),rand).mod(prime);
      } while ( g.modPow(new BigInteger("2"),prime).equals(BigInteger.ONE) || g.modPow(q,prime).equals(BigInteger.ONE) );

      // 2. Alice wysyła p i g do Boba
      clientOutput.writeUTF(prime.toString());
      clientOutput.writeUTF(g.toString());

      // 3. Alice losuje 1 <= Xa < p
      BigInteger Xa = new BigInteger(rand.nextInt(NUM_OF_BITS),rand).mod(prime);

      // 4. Alice oblicza Ya = g^Xa(mod p)
      BigInteger Ya = g.modPow(Xa,prime);

      // 5. Alice wysyła Ya do Boba
      clientOutput.writeUTF(Ya.toString());

      // 6. Alice odbiera Yb od Boba
      BigInteger Yb = new BigInteger(clientInput.readUTF());

      // 7. Alice oblicza s = Yb^Xa(mod p)
      BigInteger s = Yb.modPow(Xa,prime);

      // 8. Komunikacja przez AES
      System.out.println("Jak na razie:\n"+
        "prime = "+prime+'\n'+
        "g = "+g+'\n'+
        "Xa = "+Xa+'\n'+
        "Ya = "+Ya+'\n'+
        "Yb = "+Yb+'\n'+
        "s = "+s+'\n'
        );

        String msg = "Wiadomość do Boba.";
        System.out.println("Wiadomość: "+msg);
        msg = cw1.aes.encrypt(msg,s.toString());
        System.out.println("Po zaszfrowaniu: "+msg);
        System.out.println("Wysyłanie...");

        clientOutput.writeUTF(msg);

        msg = clientInput.readUTF();
        System.out.println("Otrzymano: "+msg);
        System.out.println("Po odszyfrowaniu: "+cw1.aes.decrypt(msg,s.toString()));


      serverSocket.close();
      clientSocket.close();
    }catch(Exception e){
      e.printStackTrace();
    }

  }
}
