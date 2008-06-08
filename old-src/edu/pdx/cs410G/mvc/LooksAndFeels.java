package edu.pdx.cs410G.mvc;

import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates a Swing GUI's ability to have a pluggable
 * look and feel.
 *
 * @see UIManager
 */
public class LooksAndFeels extends JPanel {

  /**
   * Creates a new GUI that displays several buttons that let you
   * switch between looks and feels
   *
   * @param frame
   *        The frame in which this panel is contained
   */
  public LooksAndFeels(final JFrame frame) {
    this.setLayout(new BorderLayout());

    JPanel buttons = new JPanel();
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
    ButtonGroup group = new ButtonGroup();

    // Get information about each available look and feel
    UIManager.LookAndFeelInfo[] infos =
      UIManager.getInstalledLookAndFeels();
    for (int i = 0; i < infos.length; i++) {
      UIManager.LookAndFeelInfo info = infos[i];

      // Create a radio button for each look and feel
      JRadioButton button = new JRadioButton(info.getName());
      button.setToolTipText(info.getClassName());
      group.add(button);
      buttons.add(button);

      final String className = info.getClassName();
      // When button is selected, change to that look and feel
      button.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    try {
	      System.out.println("Setting L&F to " + className);
	      UIManager.setLookAndFeel(className);
	      SwingUtilities.updateComponentTreeUI(frame);
	      frame.pack();

	    } catch (Exception ex) {
	      System.err.println("Couldn't load L&F \"" + className +
				 "\"");
	      ex.printStackTrace(System.err);
	    }
	  }
	});

      // The active look and feel should be selected
      String currentClass =
	UIManager.getLookAndFeel().getClass().getName();
      if (currentClass.equals(className)) {
	button.setSelected(true);
      }
    }
    buttons.add(Box.createVerticalGlue());

    this.add(buttons, BorderLayout.WEST);

    // Just for the sake of demonstration...
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("A menu");
    menuBar.add(menu);
    menu.add("A menu item");
    menu.add(new JRadioButtonMenuItem("Another menu item"));
    frame.setJMenuBar(menuBar);

    this.add(new JScrollPane(new JEditorPane()), BorderLayout.CENTER);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Pluggable Look and Feel");
    frame.getContentPane().add(new LooksAndFeels(frame));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
