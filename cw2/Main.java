import java.security.SecureRandom;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

public class Main {

  private static int numOfBits;
  private static int n;
  private static int k;

  private static BigInteger getPolyVal(BigInteger[] vals,BigInteger x) {
    BigInteger d = vals[0];
    BigInteger temp;
    for (int i=1; i<vals.length ;i++ ) {
      temp = BigInteger.ONE;
      for (int j=0;j<i ;j++ ) {
        temp = temp.multiply(x);
      }
      temp = temp.multiply(vals[i]);
      d = d.add(temp);
    }
    return d;
  }

  private static BigInteger getLagrInterPoly(int[] xs,BigInteger[] ys) {
    BigInteger licznikTemp, mianownikTemp, wynik;
    wynik = BigInteger.ZERO;
    for (int i = 0;i < xs.length ;i++ ) {
      licznikTemp = mianownikTemp = BigInteger.ONE;
      for (int j = 0;j < xs.length ;j++ ) {
        if (i != j) {
          licznikTemp = licznikTemp.multiply( BigInteger.valueOf(-xs[j]) );
          mianownikTemp = mianownikTemp.multiply( BigInteger.valueOf( xs[i] - xs[j] ) );
        }
      }
      wynik = wynik.add( ys[i].multiply( licznikTemp.divide(mianownikTemp) ) );
    }
    return wynik;
  }

  public static void main(String[] args) {
    numOfBits = Integer.parseInt(args[0]);
    n = Integer.parseInt(args[1]);
    k = Integer.parseInt(args[2]);

    SecureRandom rand = new SecureRandom();
    BigInteger prime = BigInteger.probablePrime(numOfBits,rand);
    System.out.println("PRIME: "+prime);

    BigInteger secret = new BigInteger(rand.nextInt(numOfBits),rand).mod(prime);
    System.out.println("SECRET: "+secret);
    BigInteger[] polynomial = new BigInteger[k];
    polynomial[0] = secret;

    for (int i=1;i<k ;i++ ) {
      polynomial[i]=new BigInteger(rand.nextInt(numOfBits),rand).mod(prime);
      System.out.println("polynomial["+i+"]: "+polynomial[i]);
    }
    //mamy: wpółczynniki wielomianu a_0,...,a_k; a_0 to sekret.
    System.out.println("poly(0) = "+getPolyVal(polynomial,BigInteger.ZERO));

    //skonstruować n punktów (x,W(x)) zaczynając od x=1
    //każdy dostałby jeden punkt, potrzebują k punktów do odtworzenia wielomianu
    BigInteger[] points = new BigInteger[n];
    for (int i=0;i<n ;i++ ) {
      points[i] = getPolyVal(polynomial, BigInteger.valueOf(i+1));
      System.out.println("poly("+(i+1)+") = "+points[i]);
    }

    //wybrać k punktów z n.
    BigInteger[] b = Arrays.copyOfRange(points,0,k);
    int[] ks = new int[k];
    for (int i = 1;i <= k ;i++ ) {
      ks[i-1] = i;
      System.out.println("Punkt "+i+": ("+ks[i-1]+","+b[i-1]+")");
    }
    //interpolacja Lagrange'a dla tych punktów. Jak to zrobić?
    System.out.println("Obliczono sekret: "+getLagrInterPoly(ks,b));
    //wyraz wolny wyznaczonego wielomianu to szukany sekret. Sprawdzić czy się zgadza.
  }
}
