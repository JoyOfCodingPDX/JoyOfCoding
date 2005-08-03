package edu.pdx.cs399J.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This program demonstrates {@link CardLayout} and provides biting
 * social commentary on my social life.
 */
public class DavesSocialLife {

  private static final String[] daysOfTheWeek = 
  { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", 
    "Saturday" };

  public static void main(String[] args) {

    final JPanel calendar = new JPanel();
    final CardLayout layout = new CardLayout();
    calendar.setLayout(layout);
    final JList list = new JList(daysOfTheWeek);
    
    for (int i = 0; i < daysOfTheWeek.length; i++) {
      String day = daysOfTheWeek[i];

      JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      p.add(new JLabel(day, JLabel.CENTER), BorderLayout.NORTH);
      calendar.add(p, day);
    }

    list.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          String day = (String) list.getSelectedValue();
          layout.show(calendar, day);
        }
      });

    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(list, BorderLayout.WEST);
    p.add(calendar, BorderLayout.CENTER);

    JFrame f = new JFrame("Daves Social Calendar");
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
