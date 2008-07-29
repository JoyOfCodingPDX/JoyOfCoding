package edu.pdx.cs399J.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.List;
import java.util.Arrays;

/**
 * The main entry point to GWT examples
 */
public class Examples implements EntryPoint {

  public void onModuleLoad() {
    TabPanel allExamples = new TabPanel();
    for (Example example : getExamples()) {
      allExamples.add(example, example.getName());
    }
    allExamples.selectTab(0);

    RootPanel.get().add(allExamples);
  }

  private List<? extends Example> getExamples() {
    return Arrays.asList(
      new LabelExample(),
      new ListBoxExample(),
      new TextBoxExample(),
      new TextAreaExample(),
      new ButtonsExample()
    );
  }
}
