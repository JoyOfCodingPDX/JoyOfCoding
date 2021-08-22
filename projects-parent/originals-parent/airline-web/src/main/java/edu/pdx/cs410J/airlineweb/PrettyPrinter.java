package edu.pdx.cs410J.airlineweb;

import com.google.common.annotations.VisibleForTesting;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

public class PrettyPrinter {
  private final Writer writer;

  @VisibleForTesting
  static String formatWordCount(int count )
  {
    return String.format( "Dictionary on server contains %d words", count );
  }

  @VisibleForTesting
  static String formatDictionaryEntry(String word, String definition )
  {
    return String.format("  %s : %s", word, definition);
  }


  public PrettyPrinter(Writer writer) {
    this.writer = writer;
  }

  public void dump(Map<String, String> dictionary) {
    try (
      PrintWriter pw = new PrintWriter(this.writer)
    ) {

      pw.println(formatWordCount(dictionary.size()));

      for (Map.Entry<String, String> entry : dictionary.entrySet()) {
        String word = entry.getKey();
        String definition = entry.getValue();
        pw.println(formatDictionaryEntry(word, definition));
      }

      pw.flush();
    }

  }
}
