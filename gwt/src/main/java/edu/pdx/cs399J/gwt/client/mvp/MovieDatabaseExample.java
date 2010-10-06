package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.pdx.cs399J.gwt.client.Example;

/**
 * Demonstrates how custom GWT events an be used to communicate between components.
 */
public class MovieDatabaseExample extends Example {

  public MovieDatabaseExample(MovieListPresenter.Display list) {
    super("Custom Events");

    DockPanel ui = new DockPanel();
    ui.add(list.asWidget(), DockPanel.WEST);
      
    add(ui);
  }
}
