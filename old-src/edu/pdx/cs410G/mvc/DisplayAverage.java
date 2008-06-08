package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;

/**
 * This class demonstrates the Model/View/Controller design pattern by
 * offering two views of a list of numbers: displaying them in a
 * {@link JList} and displaying their average.  When the underlying
 * model (a {@link ListModel}) changes, both views are updated.
 */
public class DisplayAverage extends JPanel {

  public DisplayAverage() {
    this.setLayout(new BorderLayout());

    // The data we're modeling
    final DefaultListModel model = new DefaultListModel();

    // A panel for entering a number
    JPanel enter = new JPanel();
    enter.setLayout(new BoxLayout(enter, BoxLayout.X_AXIS));
    enter.add(new JLabel("Enter a number"));
    final JTextField input = new JTextField(8);
    input.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  // When the user hits ENTER, add the number to the data model
	  model.addElement(new Integer(input.getText()));
	}
      });
    enter.add(input);
    enter.add(Box.createHorizontalGlue());
    this.add(enter, BorderLayout.NORTH);

    // A JList that displays the data in the model
    JList list = new JList(model);
    this.add(new JScrollPane(list), BorderLayout.CENTER);

    // A JLabel that displays the average of the data
    final JLabel average = new JLabel("Average:");
    this.add(average, BorderLayout.SOUTH);

    // Add a ListDataListener that acts as a controller between the
    // model and the average JLabel
    model.addListDataListener(new ListDataListener() {
	public void intervalAdded(ListDataEvent e) {
	  displayAverage();
	}
	
	public void intervalRemoved(ListDataEvent e) {
	  displayAverage();
	}

	public void contentsChanged(ListDataEvent e) {
	  displayAverage();
	}

	private void displayAverage() {
	  int size = model.getSize();
	  int total = 0;
	  for (int i = 0; i < size; i++) {
	    total += ((Integer) model.getElementAt(i)).intValue();
	  }
	  average.setText("Average: " + 
			  (((double) total)/((double) size)));
	}
      });
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("An Example of MVC");
    frame.getContentPane().add(new DisplayAverage());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
