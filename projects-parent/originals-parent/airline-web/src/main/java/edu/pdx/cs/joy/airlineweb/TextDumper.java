package edu.pdx.cs.joy.airlineweb;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;

public class TextDumper {
  private final Writer writer;

  public TextDumper(Writer writer) {
    this.writer = writer;
  }

  public void dump(Dictionary<String, String> dictionary) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ){
      Enumeration<String> words = dictionary.keys();
      while (words.hasMoreElements()) {
        String word = words.nextElement();
        pw.println(word + " : " + dictionary.get(word));
      }

      pw.flush();
    }
  }
}
