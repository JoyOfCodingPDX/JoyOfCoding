package edu.pdx.cs410J.net;

import java.net.*;

/**
 * Uses a <code>URLClassLoader</code> to load a resource from a given
 * URL. 
 */
public class LoadFromURL {

  public static void main(String[] args) throws Exception {
    String url = args[0];
    String resource = args[1];

    URL[] urls = { new URL(url) };
    URLClassLoader loader = new URLClassLoader(urls);

    URL r = loader.getResource(resource);
    System.out.println(r);
  }
}
