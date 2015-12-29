package edu.pdx.cs410J.grader;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

public class HtmlGenerator {
  public static final String DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
  private static final String TEXT = "___TEXT___";

  private final PrintWriter writer;
  private final Deque<String> openTags;

  public HtmlGenerator(Writer writer) {
    this.openTags = new ArrayDeque<>();

    this.writer = new PrintWriter(writer, true);
    this.writer.print(DOC_TYPE);
  }

  public void beginTag(String tagName) {
    indent();
    this.writer.print("<" + tagName + ">");
    this.openTags.push(tagName);
  }

  private void indent() {
    this.writer.print("\n");
    int indent = this.openTags.size() * 2;
    for (int i = 0; i < indent; i++) {
      this.writer.print(" ");
    }

  }

  public void endTag() {
    String openTag = this.openTags.pop();
    if (TEXT.equals(openTag)) {
      openTag = this.openTags.pop();

    } else {
      indent();
    }
    this.writer.print("</" + openTag + ">");
    this.writer.flush();
  }

  public void text(String text) {
    this.openTags.push(TEXT);
    this.writer.print(text);
  }
}
