package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * This GUI displays checks in a checkbook in a {@link JTable}.  The
 * checkbook is modeled by a {@link CheckbookTableModel}.
 *
 * @see CheckbookCellRenderer
 */
public class CheckbookGUI extends JPanel 
  implements CheckbookConstants {

  public CheckbookGUI() {
    this.setLayout(new BorderLayout());

    // Create the checkbook table
    final CheckbookTableModel checkbook = new CheckbookTableModel();
    JTable table = new JTable(checkbook);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    String columnName = columnNames[BALANCE_COLUMN];
    TableColumn column = table.getColumn(columnName);
    column.setCellRenderer(new CheckbookCellRenderer());

    columnName = columnNames[TRANSACTION_COLUMN];
    column = table.getColumn(columnName);
    JComboBox combo = 
      new JComboBox(new String[] { DEPOSIT, WITHDRAWL });
    column.setCellEditor(new DefaultCellEditor(combo));

    this.add(new JScrollPane(table), BorderLayout.CENTER);
    
    // Add a button to create a new check
    JButton newCheck = new JButton("Create a new check");
    newCheck.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  checkbook.createCheck();
	}
      });
    this.add(newCheck, BorderLayout.SOUTH);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("A Checkbook GUI");
    frame.getContentPane().add(new CheckbookGUI());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
