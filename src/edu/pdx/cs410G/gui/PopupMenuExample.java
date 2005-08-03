package edu.pdx.cs410G.gui;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class demonstrates a Swing popup menu
 */
public class PopupMenuExample extends JFrame {

  /**
   * Create a label whose color is selected using a {@link JPopupMenu}
   */
  public PopupMenuExample(String title) {
    super(title);

    final JLabel label = new JLabel("Your text here");
    label.setOpaque(true);
    
    label.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          if (!e.isPopupTrigger()) {
            return;
          }
          JPopupMenu menu = new JPopupMenu("Change text");
          JMenuItem item = new JMenuItem("One");
          menu.add(item);
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                label.setText("One");
              }
            });

          item = new JMenuItem("Two");
          menu.add(item);
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                label.setText("Two");
              }
            });

          item = new JMenuItem("Three");
          menu.add(item);
          item.addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                label.setText("Three");
              }
            });

          // Must add the menu to some container
          PopupMenuExample.this.add(menu);
          menu.show(e.getComponent(), e.getX(), e.getY());
        }
      });

    JMenu menu = new JMenu("Colors");

    JMenuItem item = new JMenuItem("Blue");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.blue);
        }
      });
    item.setMnemonic(KeyEvent.VK_B);

    item = new JMenuItem("Red");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.red);
        }
      });
    item.setMnemonic(KeyEvent.VK_R);

    item = new JMenuItem("Yellow");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.yellow);
        }
      });
    item.setMnemonic(KeyEvent.VK_Y);

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
    rbitem.setMnemonic(KeyEvent.VK_B);

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
    rbitem.setMnemonic(KeyEvent.VK_I);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    menuBar.add(styleMenu);
    this.setJMenuBar(menuBar);

    JPanel panel = new JPanel();
    panel.add(label);
    this.add(panel);
  }

  /**
   * Create a new {@link JFrame} and add a PopupMenuExample to it
   */
  public static void main(String[] args) {
    JFrame frame = new PopupMenuExample("PopupMenu Example");
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
