package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Button;

/**
 * Demonstrates GWT's {@link DockPanel}
 */
public class DockPanelExample extends Example {
  public DockPanelExample() {
    super("Dock Panel");
    
    DockPanel dock = new DockPanel();
    dock.add(new Button("North"), DockPanel.NORTH);
    dock.add(new Button("South"), DockPanel.SOUTH);
    dock.add(new Button("East"), DockPanel.EAST);
    dock.add(new Button("West"), DockPanel.WEST);
    dock.add(new Button("Center"), DockPanel.CENTER);
    add(dock);
  }
}
