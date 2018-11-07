import java.net.*;
import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;

public class Alice {

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
      BigInteger prime = BigInteger.probablePrime(/*numOfBits*/10,rand);
      BigInteger g = new BigInteger(rand.nextInt(/*numOfBits*/10),rand).mod(prime);

      // 2. Alice wysyła p i g do Boba
      clientOutput.writeUTF(prime.toString());
      clientOutput.writeUTF(g.toString());

      // 3. Alice losuje 1 <= Xa < p

      // 4. Alice oblicza Ya = g^Xa(mod p)

      // 5. Alice wysyła Ya do Boba

      // 6. Alice odbiera Yb od Boba

      // 7. Alice oblicza s = Yb^Xa(mod p)

      // 8. Komunikacja przez AES

      serverSocket.close();
      clientSocket.close();
    }catch(Exception e){
      e.printStackTrace();
    }

  }
}
