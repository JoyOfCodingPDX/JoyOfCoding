package edu.pdx.cs410J.examples;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This program demonstrates different features for drawing text.
 */
public class Fonts extends JPanel {

  private DrawCoords coords;
//   private String fontName;
//   private int style;
//   private int size;
//   private Color color;

  /**
   * Sets up a GUI for manipulating the way a DrawCoords draws its
   * text.
   */
  public Fonts() {
    this.setLayout(new BorderLayout());

    this.coords = new DrawCoords();
    this.add(coords, BorderLayout.CENTER);

    this.add(makeFontsList(), BorderLayout.EAST);

    JPanel panel = new JPanel();
    panel.add(makeColorChooser());
    panel.add(makeItalicsButton());
    panel.add(makeBoldButton());
    panel.add(new Label("Size:"));
    panel.add(makeSizeField());

    this.add(panel, BorderLayout.SOUTH);
  }

  /**
   * Creates a list with a scroll bar that displays the names of all
   * of the available fonts.
   */
  private Component makeFontsList() {
    // Get the list of fonts
    GraphicsEnvironment env =
      GraphicsEnvironment.getLocalGraphicsEnvironment();
    final String[] fontNames = env.getAvailableFontFamilyNames();

    // Make a JList to display the fonts
    final JList list = new JList(fontNames);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    list.addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          // Set the font and update
          int i = list.getSelectedIndex();
          if(i != -1) {
            String fontName = fontNames[i];
            Font font = coords.getFont();
            font = new Font(fontName, font.getStyle(),
                            font.getSize());
            coords.setFont(font);
            redrawCoords();
          }
        }
      });

    return(new JScrollPane(list));    
  }

  /**
   * Creates a button that when pressed invokes a color chooser so
   * that you can change the color of the text in the DrawCoords.
   */
  private Component makeColorChooser() {
    Button button = new Button("Change Color");
    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Pop up the color chooser
          Color newColor = 
            JColorChooser.showDialog(Fonts.this, "Select a Color",
                                     coords.getForeground());
          if(newColor != null) {
            coords.setForeground(newColor);
            coords.repaint();
          }
        }
      });

    return(button);
  }

  /**
   * Returns a toggle button that is use to enable/disable italicizing
   * the font.
   */
  private Component makeItalicsButton() {
    JCheckBox button = new JCheckBox("Italics", false);
    button.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          Font font = coords.getFont();
          int style = font.getStyle();
          style ^= Font.ITALIC;
          coords.setFont(font.deriveFont(style));
          coords.repaint();
        }
      });
    return(button);
  }

  /**
   * Returns a toggle button that is use to enable/disable italicizing
   * the font.
   */
  private Component makeBoldButton() {
    JCheckBox button = new JCheckBox("Bold", false);
    button.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          Font font = coords.getFont();
          int style = font.getStyle();
          style ^= Font.BOLD;
          coords.setFont(font.deriveFont(style));
          coords.repaint();
        }
      });
    return(button);
  }

  /**
   * Returns a text field that is used to enter the size of the font
   * used to draw text.
   */
  private Component makeSizeField() {
    final JTextField field = new JTextField(2);
    field.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String size = field.getText();
          try {
            Font font = coords.getFont();
            font = font.deriveFont(Float.parseFloat(size));
            coords.setFont(font);
            coords.repaint();
          } catch(NumberFormatException ex) {
            // Ignore
          }
        }
      });

    return(field);
  }

  /**
   * Redraws the DrawCoords using the selected color, font, style, and
   * size.
   */
  private void redrawCoords() {
    // Set up some default values
//     coords.setForeground(this.color);
    coords.repaint();
  }

  /**
   * Creates and displays a <code>Fonts</code>
   */
  public static void main(String[] args) {
    Fonts fonts = new Fonts();
    JFrame frame = new JFrame("Fonts demo");
    frame.getContentPane().add(fonts);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }

}
