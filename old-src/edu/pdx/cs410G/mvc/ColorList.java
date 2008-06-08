package edu.pdx.cs410G.mvc;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;

/**
 * This class demonstrates a {@link ListCellRenderer} that displays
 * the elements of a list in a particular color.
 *
 * @author David Whitlock
 * @since Fall 2005
 */
public class ColorList extends JPanel {

  /**
   * Populates a <code>ColorList</code> with a single {@link JList}
   * that contains {@link ColorList.NamedColor}s and is drawn with a
   * custom {@link ListCellRenderer}.
   */
  public ColorList() {
    DefaultListModel model = new DefaultListModel();
    model.addElement(new NamedColor(Color.RED, "Red"));
    model.addElement(new NamedColor(Color.GREEN, "Green"));
    model.addElement(new NamedColor(Color.BLUE, "Blue"));
    model.addElement(new NamedColor(Color.BLACK, "Black"));
    model.addElement(new NamedColor(Color.ORANGE, "Orange"));

    ListCellRenderer renderer = new DefaultListCellRenderer() {
        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
          JLabel label = (JLabel)
            super.getListCellRendererComponent(list, value, index, 
                                               isSelected, cellHasFocus);
          NamedColor nc = (NamedColor) value;
          label.setForeground(nc.color);
          label.setText(nc.name);

          return label;
        }
      };

    JList list = new JList(model);
    list.setCellRenderer(renderer);

    this.add(list);    
  }

  /**
   * A class that consists of a {@link Color} and its name
   */
  static class NamedColor {
    final Color color;
    final String name;

    NamedColor(Color color, String name) {
      this.color = color;
      this.name = name;
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Using a ListCellRenderer");
    frame.getContentPane().add(new ColorList());
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
