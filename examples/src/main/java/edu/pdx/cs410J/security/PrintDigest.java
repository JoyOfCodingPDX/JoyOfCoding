package edu.pdx.cs410J.security;

import java.security.*;

/**
 * This program computes the MD5 digest of a sentence specified on the
 * command line.
 */
public class PrintDigest {

  public static void main(String[] args) {
    String message = args[0];
    MessageDigest algorithm = null;
    try {
      algorithm = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    algorithm.reset();
    algorithm.update(message.getBytes());
    byte[] digest = algorithm.digest();

    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < digest.length; i++) {
      String s = Integer.toHexString(0xFF & digest[i]);
      hexString.append(s);
    }
    
    System.out.println("Message: " + message);
    System.out.println("Digest: " + hexString.toString());
  }
}
