package edu.pdx.cs410J.security;

import java.io.*;
import java.security.*;

/**
 * This program generates a pair of DSA keys in files named
 * <code>public.key</code> and <code>private.key</code>
 */
public class GenerateKeys {
  public static void main(String[] args) {
    KeyPairGenerator gen = null;

    try {
      gen = KeyPairGenerator.getInstance("DSA");
    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace(System.err);
    }

    gen.initialize(1024);
    KeyPair keys = gen.generateKeyPair();

    PrivateKey privateKey = keys.getPrivate();
    PublicKey publicKey = keys.getPublic();

    try {
      FileOutputStream fos = new FileOutputStream ("private.key");
      fos.write(privateKey.getEncoded());

      fos = new FileOutputStream ("public.key");
      fos.write(publicKey.getEncoded());

    } catch (IOException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }
}
