package edu.pdx.cs410J.security;

import java.io.*;
import java.security.*;
import java.security.spec.*;

/**
 * This program verifies that a given message was signed by the person
 * with a given public key.
 */
public class VerifyMessage {
  public static void main(String[] args) {
    String fileName = args[0];
    String digestName = args[1];
    String message = args[2];

    try {
      // Read in key from file
      FileInputStream fis = new FileInputStream(fileName);
      byte[] encodedKey = new byte[fis.available()];
      fis.read(encodedKey);
      fis.close();

      X509EncodedKeySpec spec =
        new X509EncodedKeySpec(encodedKey);
      KeyFactory factory =
        KeyFactory.getInstance("DSA", "SUN");
      PublicKey publicKey = factory.generatePublic(spec);

      // Read the digest
      fis = new FileInputStream(digestName);
      byte[] digest = new byte[fis.available()];
      fis.read(digest);
      fis.close();

      // Compute DSA signature
      Signature sig = Signature.getInstance("DSA");

      sig.initVerify(publicKey);
      sig.update(message.getBytes());
      if (sig.verify(digest)) {
	System.out.println("Success");
      } else {
	System.out.println("Failure");
      }
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
