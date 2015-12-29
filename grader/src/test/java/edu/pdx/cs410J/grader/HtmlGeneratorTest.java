package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class HtmlGeneratorTest {

  @Test
  public void doctypeIsWritten() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.endTag();

    String html = writer.toString();
    assertThat(html, containsString(HtmlGenerator.DOC_TYPE));
  }

  @Test
  public void beginningATagOpensThatTag() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");

    String html = writer.toString();
    assertThat(html, containsString("<html>"));
  }

  @Test
  public void endingATagClosesTheLastOpenedTag() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.endTag();

    String html = writer.toString();
    assertThat(html, containsString("<html>"));
    assertThat(html, containsString("</html>"));
  }

  @Test
  public void nestedTags() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.beginTag("head");
    generator.endTag();
    generator.endTag();

    String html = writer.toString();
    assertThat(html, containsString("<html>"));
    assertThat(html, containsString("<head>"));
    assertThat(html, containsString("</head>"));
    assertThat(html, containsString("</html>"));
  }

  @Test
  public void tagsAreOnTheirOwnLine() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.endTag();

    String html = writer.toString();
    String[] lines = parseIntoLines(html);
    assertThat(lines[1], equalTo("<html>"));
    assertThat(lines[2], equalTo("</html>"));
  }

  private String[] parseIntoLines(String html) {
    return html.split("\n");
  }

  @Test
  public void nestedTagsAreIndented() {
    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.beginTag("head");
    generator.beginTag("title");
    generator.endTag();
    generator.endTag();
    generator.endTag();

    String html = writer.toString();
    String[] lines = parseIntoLines(html);
    assertThat(lines[1], equalTo("<html>"));
    assertThat(lines[2], equalTo("  <head>"));
    assertThat(lines[3], equalTo("    <title>"));
    assertThat(lines[4], equalTo("    </title>"));
    assertThat(lines[5], equalTo("  </head>"));
    assertThat(lines[6], equalTo("</html>"));
  }

  @Test
  public void textIsWritten() {
    String text = "This is some text";

    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.beginTag("head");
    generator.beginTag("title");
    generator.text(text);
    generator.endTag();
    generator.endTag();
    generator.endTag();

    String html = writer.toString();
    assertThat(html, containsString(text));
  }

  @Test
  public void textIsWrittenOnTheSameLineAsOpenTag() {
    String text = "This is some text";

    StringWriter writer = new StringWriter();
    HtmlGenerator generator = new HtmlGenerator(writer);
    generator.beginTag("html");
    generator.beginTag("head");
    generator.beginTag("title");
    generator.text(text);
    generator.endTag();
    generator.endTag();
    generator.endTag();

    String html = writer.toString();
    assertThat(html, containsString("<title>" + text + "</title>"));
  }

}
