package edu.pdx.cs399J.gui;

import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates handling mouse and key events
 */
public class MouseAndKeyEvents extends JPanel {

  /**
   * Create a {@link JButton button} so that there is something in the
   * panel
   */
  public MouseAndKeyEvents() {
    final JTextField x = new JTextField(3);
    final JTextField y = new JTextField(3);
    final JTextField c = new JTextField(1);

    this.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          x.setText(e.getX() + "");
          y.setText(e.getY() + "");
        }
      });

    JButton button = new JButton("Type Here");
    button.addKeyListener(new KeyAdapter() {
        public void keyTyped(KeyEvent e) {
          c.setText(e.getKeyChar() + "");
        }
      });

    x.setEditable(false); this.add(x);
    y.setEditable(false); this.add(y);
    c.setEditable(false); this.add(c);
    this.add(button);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a MouseAndKeyEvents
   * to it 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Mouse and key events");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // The frame is being closed, exit the JVM
          System.exit(0);
        }
      });
    frame.add(new MouseAndKeyEvents());
    frame.pack();
    frame.setVisible(true);
  }

}
