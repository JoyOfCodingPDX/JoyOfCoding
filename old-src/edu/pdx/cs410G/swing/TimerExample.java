package edu.pdx.cs410G.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.*;

/**
 * This program demonstrates a Swing {@link Timer} that periodically
 * updates a {@link JTextField} with the current time.
 */
public class TimerExample extends JPanel {

  private final Timer timer;

  public TimerExample() {
    this.setLayout(new BorderLayout());
    final JTextField text = new JTextField(20);
    text.setEditable(false);
    this.add(BorderLayout.CENTER, text);
    
    ActionListener listener = new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  text.setText(new Date().toString());
	}
      };
    this.timer = new Timer(1000, listener);
    this.timer.setInitialDelay(0);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("A Timer");
    TimerExample example = new TimerExample();
    frame.getContentPane().add(example);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    // Start timer after GUI is visible
    example.timer.start();
  }

}
