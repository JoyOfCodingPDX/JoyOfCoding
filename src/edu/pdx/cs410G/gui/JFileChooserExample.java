package edu.pdx.cs399J.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * This program demonstrates Swing's {@link JFileChooser} container.
 */
public class JFileChooserExample extends JPanel {

  public JFileChooserExample(final JFrame parent) {
    this.setLayout(new FlowLayout());

    JButton choose = new JButton("Choose file");
    choose.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String title = "Select a text file";
          JFileChooser chooser = new JFileChooser();
          int mode = JFileChooser.FILES_ONLY;
          chooser.setFileSelectionMode(mode);
          chooser.setFileFilter(new FileFilter() {
              public boolean accept(File file) {
                return file.getName().endsWith(".txt");
              }

              public String getDescription() {
                return "Text files (*.txt)";
              }
            });
          int status = chooser.showOpenDialog(parent);
          if (status == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            System.out.println("You chose: " + file);
          }
        }
      });
    this.add(choose);
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JFileChooser example");
    JPanel panel = new JFileChooserExample(frame);
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
