package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This GUI displays checks in a checkbook in a {@link JTable}.  The
 * checkbook is modeled by a {@link CheckbookTableModel}.
 */
public class CheckbookGUI extends JPanel {

  public CheckbookGUI() {
    this.setLayout(new BorderLayout());

    // Create the checkbook table
    final CheckbookTableModel checkbook = new CheckbookTableModel();
    this.add(new JScrollPane(new JTable(checkbook)),
             BorderLayout.CENTER);
    
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
