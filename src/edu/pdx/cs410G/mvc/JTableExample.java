package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.table.*;

/**
 * This program demonstrates how to use a {@link DefaultTableModel} to
 * display a two-dimensional array of data in a {@link JTree}.
 */
public class JTableExample extends JPanel {

  public JTableExample() {
    this.setLayout(new BorderLayout());

    // Create some data
    String[] columnNames = { "Name", "Quiz1", "Quiz2", "Quiz3" };
    Object[][] data = {
      { "Mary", new Double(85.0), new Double(92.5), new Double(87.4) },
      { "Sunil", new Double(94.3), new Double(82.9), new Double(97.0) },
      { "Fred", new Double(84.0), new Double(72.5), new Double(98.6) },
      { "Jin", new Double(98.2), new Double(92.5), new Double(78.4) },
    };

    // Create a tabel model based on that data
    TableModel model = new DefaultTableModel(data, columnNames);
    
    // Create a JTable to display the data.  Note that the table
    // heading will not be displayed automatically unless you place in
    // the table in JScrollPane.
    JTable tree = new JTable(model);
    this.add(new JScrollPane(tree), BorderLayout.CENTER);
  }
  
  public static void main(String[] args) {
    JFrame frame = new JFrame("An Example of a JTable");
    frame.getContentPane().add(new JTableExample());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
