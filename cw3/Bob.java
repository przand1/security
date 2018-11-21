package cw3;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;

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

      // 1. Bob odbiera p i g od Alice
      BigInteger prime = new BigInteger(clientInput.readUTF());
      BigInteger g = new BigInteger(clientInput.readUTF());

      // 2. Bob losuje 1 <= Xb < p
      BigInteger Xb = new BigInteger(rand.nextInt(NUM_OF_BITS),rand).mod(prime);

      // 3. Bob oblicza Yb = g^Xb(mod p)
      BigInteger Yb = g.modPow(Xb,prime);

      // 4. Bob odbiera Ya od Alice
      BigInteger Ya = new BigInteger(clientInput.readUTF());

      // 4. Bob wysyła Yb do Alice
      clientOutput.writeUTF(Yb.toString());

      // 5. Bob oblicza s = Ya^Xb(mod p)
      BigInteger s = Ya.modPow(Xb,prime);

      // 8. Komunikacja przez AES
      System.out.println("Jak na razie:\n"+
        "prime = "+prime+'\n'+
        "g = "+g+'\n'+
        "Xb = "+Xb+'\n'+
        "Yb = "+Yb+'\n'+
        "Ya = "+Ya+'\n'+
        "s = "+s+'\n'
        );


        String msg = clientInput.readUTF();
        System.out.println("Otrzymano: "+msg);
        System.out.println("Po odszyfrowaniu: "+cw1.aes.decrypt(msg,s.toString()));

        msg = "Wiadomość do Alice.";
        System.out.println("Wiadomość: "+msg);
        msg = cw1.aes.encrypt(msg,s.toString());
        System.out.println("Po zaszfrowaniu: "+msg);
        System.out.println("Wysyłanie...");

        clientOutput.writeUTF(msg);

      clientSocket.close();
    } catch (ConnectException c) {
      System.out.println("Connection refused. Is server on?");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
