package edu.pdx.cs410J.security;

import java.io.*;
import java.security.*;
import java.security.spec.*;

/**
 * This program computes the DSA digital signature of a message using
 * a private key.  The digest is written to a file named
 * <code>digest.out</code>.
 */
public class SignMessage {
  public static void main(String[] args) {
    String fileName = args[0];
    String message = args[1];

    try {
      // Read in key from file
      FileInputStream fis = new FileInputStream(fileName);
      byte[] encodedKey = new byte[fis.available()];
      fis.read(encodedKey);
      fis.close();

      PKCS8EncodedKeySpec spec = 
	new PKCS8EncodedKeySpec(encodedKey);
      KeyFactory factory = 
	KeyFactory.getInstance("DSA", "SUN");
      PrivateKey privateKey = factory.generatePrivate(spec);

      // Compute DSA signature
      Signature sig = Signature.getInstance("DSA");

      sig.initSign(privateKey);
      sig.update(message.getBytes());
      byte[] digest = sig.sign();

      FileOutputStream fos = new FileOutputStream("digest.out");
      fos.write(digest);
      return;

    } catch (IOException ex) {
      ex.printStackTrace(System.err);

    } catch (NoSuchProviderException ex) {
      ex.printStackTrace(System.err);

    } catch (NoSuchAlgorithmException ex) {
      ex.printStackTrace(System.err);

    } catch (InvalidKeySpecException ex) {
      ex.printStackTrace(System.err);

    } catch (SignatureException ex) {
      ex.printStackTrace(System.err);

    } catch (InvalidKeyException ex) {
      ex.printStackTrace(System.err);
    }

    System.exit(1);
  }
}
