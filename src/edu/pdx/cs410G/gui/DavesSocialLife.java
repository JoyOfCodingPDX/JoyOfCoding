package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This program demonstrates {@link CardLayout} and provides biting
 * social commentary on my social life.
 */
public class DavesSocialLife {

  private static final String[] daysOfTheWeek = 
  { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", 
    "Saturday" };

  public static void main(String[] args) {

    final Panel calendar = new Panel();
    final CardLayout layout = new CardLayout();
    calendar.setLayout(layout);
    final List list = new List(daysOfTheWeek.length, false);
    
    for (int i = 0; i < daysOfTheWeek.length; i++) {
      String day = daysOfTheWeek[i];
      list.add(day);

      Panel p = new Panel();
      p.setLayout(new BorderLayout());
      p.add(new Label(day, Label.CENTER), BorderLayout.NORTH);
      calendar.add(p, day);
    }

    list.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          String day = list.getSelectedItem();
          layout.show(calendar, day);
        }
      });

    Panel p = new Panel();
    p.setLayout(new BorderLayout());
    p.add(list, BorderLayout.WEST);
    p.add(calendar, BorderLayout.CENTER);

    Frame f = new Frame("Daves Social Calendar");
    f.setLayout(new BorderLayout());
    f.add(p, BorderLayout.CENTER);

    f.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    f.pack();
    f.setVisible(true);
  }
}
