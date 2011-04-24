package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalSplitPanel;

/**
 * Demonstrates GWT's
 */
public class SplitPanelExample extends Example {
  public SplitPanelExample() {
    super("Split Panels");

    HorizontalSplitPanel horiz = new HorizontalSplitPanel();
    horiz.setWidth("250px");
    horiz.setHeight("250px");
    horiz.setLeftWidget(new Label("Left"));
    horiz.setRightWidget(new Label("Right"));
//    horiz.setSplitPosition("10px");
    add(horiz);

    VerticalSplitPanel vert = new VerticalSplitPanel();
    vert.setWidth("250px");
    vert.setHeight("250px");
    vert.setTopWidget(new Label("Top"));
    vert.setBottomWidget(new Label("Bottom"));
//    vert.setSplitPosition("15px");
    add(vert);

  }
}
