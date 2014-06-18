package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Button;

/**
 * Demonstrates GWT's {@link HorizontalPanel} and {@link VerticalPanel}
 */
public class AxisPanelExample extends Example {
  public AxisPanelExample() {
    super("Axis Panels");

    HorizontalPanel horiz = new HorizontalPanel();
    horiz.add(new Button("A"));
    horiz.add(new Button("B"));
    horiz.add(new Button("C"));
    add(horiz);

    VerticalPanel vert = new VerticalPanel();
    vert.add(new Label("1"));
    vert.add(new Label("2"));
    vert.add(new Label("3"));
    add(vert);
  }
}
