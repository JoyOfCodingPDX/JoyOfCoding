package edu.pdx.cs410J.grader;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;

public class HtmlGenerator {
  public static final String DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

  private final PrintWriter writer;
  private final Deque<String> openTags;

  public HtmlGenerator(Writer writer) {
    this.openTags = new ArrayDeque<>();

    this.writer = new PrintWriter(writer);
    this.writer.println(DOC_TYPE);
  }

  public void beginTag(String tagName) {
    this.writer.println("<" + tagName + ">");
    this.openTags.push(tagName);
  }

  public void endTag() {
    String openTag = this.openTags.pop();
    this.writer.print("</" + openTag + ">");
  }
}
