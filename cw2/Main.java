import java.security.SecureRandom;
import java.io.*;
import java.math.BigInteger;
import java.math.BigDecimal;

public class Main {

  private static final int numOfBits = 100;

  private BigDecimal getPolyVal(BigInteger[] vals, int n,int x) {
    BigDecimal d = vals[0];
    BigDecimal temp;
    for (int i=1; i<n ;i++ ) {
      temp = vals[i];
      for (int j=0;j<i ;j++ ) {
        temp *= x;
      }
      d += temp;
    }
    return d;
  }

  public static void main(String[] args) {

    SecureRandom rand = new SecureRandom();
    BigInteger prime = BigInteger.probablePrime(numOfBits,rand);
    System.out.println("PRIME: "+prime);

    BigInteger secret = new BigInteger(rand.nextInt(numOfBits)-1,rand);
    System.out.println("SECRET: "+secret);
    int n = 6;
    int k = 3;
    BigInteger[] polynomial = new BigInteger[n];
    polynomial[0] = secret;

    for (int i=1;i<n ;i++ ) {
      polynomial[i]=new BigInteger(rand.nextInt(numOfBits)-1,rand);
      System.out.println(polynomial[i]);
    }


  }
}
