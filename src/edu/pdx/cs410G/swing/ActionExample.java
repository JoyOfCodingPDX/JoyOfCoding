package edu.pdx.cs410G.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates Swing's {@link Action} interface.  The
 * panel contains a toolbar, a menu, and a text area.  When the
 * "Print" action is performed (either by selecting the menu item or
 * by clicking the button in the tool bar), the text in the text area
 * is printed to standard out.
 */
public class ActionExample extends JApplet {

  public ActionExample() {
    Container p = this.getContentPane();
    p.setLayout(new BorderLayout());

    final JTextArea text = new JTextArea(10, 30);
    p.add(text, BorderLayout.CENTER);

    // Make a new action
    Action print = new AbstractAction("Print") {
        public void actionPerformed(ActionEvent e) {
          System.out.println(text.getText());
        }
      };
    print.putValue(Action.ACCELERATOR_KEY,
                   KeyStroke.getKeyStroke("control P"));
    print.putValue(Action.SHORT_DESCRIPTION,
                   "Print text to standard out");

    // Add a menu item that performs the action
    JMenuBar menuBar = new JMenuBar();
    JMenu actions = new JMenu("Actions");
    actions.add(new JMenuItem(print));
    menuBar.add(actions);
    this.setJMenuBar(menuBar);

    // Add a tool bar with a button that performs the action
    JToolBar bar = new JToolBar(JToolBar.HORIZONTAL);
    bar.setFloatable(false);
    bar.add(new JButton(print));
    p.add(bar, BorderLayout.NORTH);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Swing Action example");
    Panel panel = new ActionExample();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
