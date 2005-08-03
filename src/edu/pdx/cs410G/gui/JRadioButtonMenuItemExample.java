package edu.pdx.cs410G.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates using Swing radio button menus
 */
public class JRadioButtonMenuItemExample extends JFrame {

  /**
   * Create a label whose color is selected using a {@link JMenu}
   */
  public JRadioButtonMenuItemExample(String title) {
    super(title);

    final JLabel label = new JLabel("Your text here");
    label.setOpaque(true);
    
    JMenu menu = new JMenu("Colors");

    JMenuItem item = new JMenuItem("Blue");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.blue);
        }
      });

    item = new JMenuItem("Red");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.red);
        }
      });

    item = new JMenuItem("Yellow");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.yellow);
        }
      });

    JMenu styleMenu = new JMenu("Style");
    
    JRadioButtonMenuItem rbitem = new JRadioButtonMenuItem("Bold", false);
    styleMenu.add(rbitem);
    rbitem.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          int style = label.getFont().getStyle();
          if (e.getStateChange() == ItemEvent.SELECTED) {
            style |= Font.BOLD;

          } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            style &= ~Font.BOLD;
          }
          label.setFont(label.getFont().deriveFont(style));
        }
      });

    rbitem = new JRadioButtonMenuItem("Italic", false);
    styleMenu.add(rbitem);
    rbitem.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          int style = label.getFont().getStyle();
          if (e.getStateChange() == ItemEvent.SELECTED) {
            style |= Font.ITALIC;

          } else if (e.getStateChange() == ItemEvent.DESELECTED) {
            style &= ~Font.ITALIC;
          }
          label.setFont(label.getFont().deriveFont(style));
        }
      });

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    menuBar.add(styleMenu);
    this.setJMenuBar(menuBar);

    JPanel panel = new JPanel();
    panel.add(label);
    this.add(panel);
  }

  /**
   * Create a new {@link JFrame} and add a MenuExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new JRadioButtonMenuItemExample("JRadioButtonMenuItem Example");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // The frame is being closed, exit the JVM
          System.exit(0);
        }
      });
    frame.pack();
    frame.setVisible(true);
  }

}
