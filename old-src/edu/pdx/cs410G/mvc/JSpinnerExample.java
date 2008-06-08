package edu.pdx.cs410G.mvc;

import javax.swing.*;


/**
 * This program demonstrates the {@link JSpinner} widget and several
 * of the standard {@link SpinnerModel} models.
 */
public class JSpinnerExample extends JPanel {

  public JSpinnerExample() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    // Default spinner (SpinnerNumberModel);
    this.add(new JSpinner());

    // Date spinner
    this.add(new JSpinner(new SpinnerDateModel()));

    // List spinner
    Object[] list = 
    { "Sunday", "Monday", "Happy Days", "Tuesday", "Wednesday", 
      "Happy Days", "Thursday", "Friday", "Happy Days" };
    this.add(new JSpinner(new SpinnerListModel(list)));

    this.add(Box.createVerticalGlue());
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("An Example of JSpinner");
    frame.getContentPane().add(new JSpinnerExample());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
