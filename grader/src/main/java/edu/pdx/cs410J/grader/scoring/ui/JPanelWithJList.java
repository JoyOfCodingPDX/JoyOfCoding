package edu.pdx.cs410J.grader.scoring.ui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.util.function.Consumer;

public class JPanelWithJList extends JPanel {

  private boolean isFinalEventInUserSelection(ListSelectionEvent e) {
    return !e.getValueIsAdjusting();
  }

  protected void registerListenerOnListItemSelection(JList<String> list, Consumer<Integer> onSelection) {
    list.addListSelectionListener(e -> {
      if (isFinalEventInUserSelection(e)) {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex >= 0) {
          onSelection.accept(selectedIndex);
        }
      }
    });
  }
}
