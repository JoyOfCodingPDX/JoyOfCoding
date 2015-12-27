package edu.pdx.cs410J.grader;

import org.junit.Test;

import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

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

}
