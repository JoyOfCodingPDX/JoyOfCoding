package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The abstract superclass of GWT examples
 */
public abstract class Example extends VerticalPanel {
  private final String name;

  /**
   * Adds the exemplar widgets to this panel
   * @param name The name of this example
   */
  protected Example(String name) {
    this.name = name;
  }

  /**
   * Returns the name of this example
   */
  public String getName() {
    return this.name;
  }

}
