import java.net.*;
import java.io.*;
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
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

      // 1. Bob odbiera p i g od Alice
      BigInteger prime = new BigInteger(clientInput.readUTF());
      BigInteger g = new BigInteger(clientInput.readUTF());
      System.out.println("Odebrano: prime = "+prime+", g = "+g);

      // 2. Bob losuje 1 <= Xb < p

      // 3. Bob oblicza Yb = g^Xb(mod p)

      // 4. Bob odbiera Ya od Alice

      // 4. Bob wysyła Yb do Alice

      // 5. Bob oblicza s = Ya^Xb(mod p)

      // 8. Komunikacja przez AES
      
      clientSocket.close();
    } catch (ConnectException c) {
      System.out.println("Connection refused. Is server on?");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
