package edu.pdx.cs410J.grader.poa.ui;

import org.junit.Test;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class HTMLEditorKitTest {

  @Test
  public void canParseHtmlFromAnEmail() throws IOException, BadLocationException {
    InputStream resource = getClass().getResourceAsStream("HtmlFromAnEmail.html");
    Reader reader = new InputStreamReader(resource);

    HTMLEditorKit kit = new HTMLEditorKit();
    HTMLDocument document = new HTMLDocument();
    document.getDocumentProperties().put("IgnoreCharsetDirective", true);
    kit.read(reader, document, 0);

    StringWriter writer = new StringWriter();
    kit.write(writer, document, 0, document.getLength());

    String renderedHtml = writer.toString();

    assertThat(renderedHtml, containsString("short of one thing in particular"));
  }
}
