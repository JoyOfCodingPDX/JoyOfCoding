package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;

/**
 * This class demonstrates using AWT checkbox menus
 */
public class CheckboxMenuItemExample extends Frame {

  /**
   * Create a label whose color is selected using a {@link
   * java.awt.Menu Menu}
   */
  public CheckboxMenuItemExample(String title) {
    super(title);

    final Label label = new Label("Your text here");
    
    Menu menu = new Menu("Colors");

    MenuItem item = new MenuItem("Blue");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.blue);
        }
      });

    item = new MenuItem("Red");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.red);
        }
      });

    item = new MenuItem("Yellow");
    menu.add(item);
    item.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          label.setBackground(Color.yellow);
        }
      });

    Menu styleMenu = new Menu("Style");
    
    CheckboxMenuItem cbitem = new CheckboxMenuItem("Bold", false);
    styleMenu.add(cbitem);
    cbitem.addItemListener(new ItemListener() {
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

    cbitem = new CheckboxMenuItem("Italic", false);
    styleMenu.add(cbitem);
    cbitem.addItemListener(new ItemListener() {
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

    MenuBar menuBar = new MenuBar();
    menuBar.add(menu);
    menuBar.add(styleMenu);
    this.setMenuBar(menuBar);

    Panel panel = new Panel();
    panel.add(label);
    this.add(panel);
  }

  /**
   * Create a new {@link java.awt.Frame Frame} and add a MenuExample
   * to it 
   */
  public static void main(String[] args) {
    Frame frame = new CheckboxMenuItemExample("CheckboxMenu Example");
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
