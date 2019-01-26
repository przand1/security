package cw6;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

public class Test2 {
  public static void main(String[] args) {
      System.out.println(getMD());
      System.out.println(getMD());
      System.out.println(getMD());
      System.out.println(getMD());
      System.out.println(getMD());
      System.out.println(getMD());
  }

  static byte[] getMD() {
    try {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    md.update((byte)1);
    md.update((byte)23);
    return md.digest();
  }
  catch(NoSuchAlgorithmException n) {
    System.out.println("NoSuchAlgorithmException");
  }
  return new byte[0];
  }
}
