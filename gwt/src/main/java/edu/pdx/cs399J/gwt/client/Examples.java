package edu.pdx.cs399J.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.*;
import edu.pdx.cs399J.gwt.client.mvp.DivisionPresenter;
import edu.pdx.cs399J.gwt.client.mvp.DivisionView;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The main entry point to GWT examples
 */
public class Examples implements EntryPoint {

  public void onModuleLoad() {
    RootPanel rootPanel = RootPanel.get("examplesDiv");

    if (rootPanel == null) {
        return;
    }

    TabPanel allExamples = new TabPanel();
    Map<String, List<? extends Example>> examples = getExamples();
    for (String title : examples.keySet()) {
      ExamplePanel panel = new ExamplePanel(title);
      for (Example example : examples.get(title)) {
        panel.add(example, example.getName());
      }
      panel.selectFirst();
      allExamples.add(panel, panel.getTitle());
    }
    allExamples.selectTab(0);

    addHistoryListener(allExamples);

    rootPanel.add(allExamples);

    History.fireCurrentHistoryState();
  }

    /**
     * Adds a new GWT history token whenever a new tab is selected
     */
  private void addHistoryListener( final TabPanel tabs ) {
    tabs.addSelectionHandler( new SelectionHandler<Integer>() {
        public void onSelection( SelectionEvent<Integer> event )
        {
            ExamplePanel panel = (ExamplePanel) tabs.getWidget( event.getSelectedItem() );
            History.newItem( panel.getTitle(), false /* Don't fire event */);
        }
    });

    History.addValueChangeHandler(new ValueChangeHandler<String>() {
      public void onValueChange(ValueChangeEvent<String> event) {
        String[] tokens = event.getValue().split("\\|");
        if (tokens.length >= 1) {
          String tabName = tokens[0];
          for (Widget w : tabs) {
            ExamplePanel panel = (ExamplePanel) w;
            if (panel.getTitle().equals(tabName)) {
              int index = tabs.getWidgetIndex(panel);
              if (tabs.getTabBar().getSelectedTab() != index) {
                tabs.selectTab(index);
              }
            }

            if (tokens.length >= 2) {
              String exampleName = tokens[1];
              panel.selectExample(exampleName);

            } else {
              panel.selectFirstExample();
            }
          }
        }
      }
    });
  }

    private Map<String, List<? extends Example>> getExamples() {
    Map<String, List<? extends Example>> examples = new LinkedHashMap<String, List<? extends Example>>();
    examples.put("Widgets",
      Arrays.asList(
        new LabelExample(),
        new ListBoxExample(),
        new TextBoxExample(),
        new TextAreaExample(),
        new ButtonsExample()
    ));
    examples.put("Panels",
      Arrays.asList(
        new AxisPanelExample(),
        new DockPanelExample(),
        new DeckPanelExample(),
        new StackPanelExample(),
        new TabPanelExample()
      ));
    examples.put("Events",
      Arrays.asList(
        new ClickHandlerExample(),
        new FocusPanelExample(),
        new DialogBoxExample()
      ));
    examples.put("Client/Server",
      Arrays.asList(
        new TimerExample(),
        new DivisionServiceExample()
      ));
    examples.put("Localization",
      Arrays.asList(
        new DateLocalizationExample()
      ));

    DivisionView view = new DivisionView( GWT.<DivisionView.Binder>create( DivisionView.Binder.class ));
    new DivisionPresenter( view, DivisionService.Helper.getAsync() );
    examples.put("Model/View/Presenter",
      Arrays.asList(
        view
      ));
    return examples;
  }

  private class ExamplePanel extends DockPanel {
    private VerticalPanel buttons = new VerticalPanel();
    private DeckPanel deck = new DeckPanel();
    private final String title;

    public ExamplePanel( String title ) {
      this.title = title;
      this.setSpacing(3);
      add(buttons, WEST);
      add(deck, CENTER);
    }

    public void add(final Example example, final String name) {
      deck.add(example);
      buttons.add(new ToggleButton(name, new ClickHandler() {
        public void onClick(ClickEvent event) {
          for (Widget w : buttons) {
            if (!w.equals(event.getSource())) {
              ((ToggleButton) w).setDown(false);
            }
          }

          deck.showWidget(deck.getWidgetIndex(example));
          History.newItem(title + "|" + name, false /* Don't fire event */);
        }
      }));
    }

    public void selectFirst() {
      ((ToggleButton) buttons.getWidget(0)).setDown(true);
      deck.showWidget(0);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    /**
     * Selects the example with the given name
     */
    public void selectExample(String name) {
      for (Widget w : buttons) {
        ToggleButton button = (ToggleButton) w;
        if (button.getText().equals(name)) {
          selectButton(button);

        } else {
          button.setDown(false);
        }

      }
    }

    private void selectButton(ToggleButton button) {
      button.setDown(true);
      deck.showWidget(buttons.getWidgetIndex(button));
    }

    public void selectFirstExample() {
      for (int i = 0; i < buttons.getWidgetCount(); i++) {
        ToggleButton button = (ToggleButton) buttons.getWidget(i);
        if (i == 0) {
          selectButton(button);

        } else {
          button.setDown(false);
        }
      }
    }
  }
}
