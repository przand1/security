package cw6;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

public class Test {
  public static void main(String[] args) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update((byte)(1));
      md.update((byte)96);
      byte[] bytes = md.digest();
      for (byte b : bytes) {
        System.out.printf("0x%02X ",b);
      }
    }
    catch(NoSuchAlgorithmException n) {
      System.out.println("NoSuchAlgorithmException");
    }
  }
}
