package edu.pdx.cs.joy.security;

import java.security.*;

/**
 * This program computes the SHA digest of a sentence specified on the
 * command line.
 */
public class PrintDigest {

  public static void main(String[] args) {
    String message = String.join(" ", args);
    MessageDigest algorithm = null;
    try {
      algorithm = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    algorithm.reset();
    algorithm.update(message.getBytes());
    byte[] digest = algorithm.digest();

    StringBuilder hexString = new StringBuilder();
    for (byte b : digest) {
      String s = Integer.toHexString(0xFF & b);
      hexString.append(s);
    }
    
    System.out.println("Message: " + message);
    System.out.println("Digest: " + hexString);
  }
}
