package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * Demonstrates the GWT {@link TextArea} and {@link RichTextArea}
 */
public class TextAreaExample extends Example {
  public TextAreaExample() {
    super("Text Area");

    TextArea text = new TextArea();
    text.setCharacterWidth(30);
    text.setVisibleLines(5);
    add(text);

    RichTextArea rich = new RichTextArea();
    rich.setHTML("Rich <i>text</i> <b>area</b>");
    add(rich);
  }
}
