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
    Timer timer = new Timer(1000, listener);
    timer.setInitialDelay(0);
    timer.start();
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("A Timer");
    JPanel panel = new TimerExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
