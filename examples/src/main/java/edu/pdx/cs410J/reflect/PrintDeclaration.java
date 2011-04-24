package edu.pdx.cs410J.reflect;

import java.io.*;
import java.net.URL;

/**
 * This program reads a resource containing the text of the
 * Declaration of Independence and prints it to standard out.
 */
public class PrintDeclaration {

  public static void main(String[] args) {
    String absoluteName = "/edu/pdx/cs410J/reflect/doi.txt";
    Class c = PrintDeclaration.class;
    URL url = c.getResource(absoluteName);
    System.out.println(url);
    
    try {
      InputStream is = c.getResourceAsStream("doi.txt");
      BufferedReader br = 
        new BufferedReader(new InputStreamReader(is));
      while (br.ready()) {
        System.out.println(br.readLine());
      }

    } catch (IOException ex) {
      ex.printStackTrace(System.err);
    }
  }

}
