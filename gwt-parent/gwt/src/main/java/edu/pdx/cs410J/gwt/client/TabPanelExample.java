package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Button;

/**
 * Demonstrates GWT's {@link TabPanel}
 */
public class TabPanelExample extends Example {
  public TabPanelExample() {
    super("Tab Panel");

    TabPanel tabs = new TabPanel();
    tabs.add(new Button("One"), "One");
    tabs.add(new Button("Two"), "Two");
    tabs.add(new Button("Three"), "Three");
    tabs.add(new Button("Four"), "Four");
    tabs.selectTab(0);
    add(tabs);
  }
}
