package edu.pdx.cs410J.gui;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This program uses {@link JEditorPane} to make a simple web browser.
 */
public class JWebBrowser extends JPanel {
  static final String HOME = "http://www.cs.pdx.edu/";

  public JWebBrowser() throws IOException {
    this.setLayout(new BorderLayout());

    final JEditorPane browser = new JEditorPane(HOME);
    browser.setEditable(false);
    browser.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent e) {
          try {
            browser.setPage(e.getURL());

          } catch(Exception ex) {
            // Ignore
          }
        }
      });
    
    // Automatically scroll
    this.add(new JScrollPane(browser), BorderLayout.CENTER);

    final JTextField loc = new JTextField(browser.getPage().toString());
    this.add(loc, BorderLayout.SOUTH);
    loc.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          try {
            browser.setPage(new java.net.URL(loc.getText()));

          } catch (Exception ex) {
            // Ignore
          }
        }
      });
  }

  public static void main(String[] args) throws IOException {
    JFrame frame = new JFrame("A simple web browser");
    JPanel panel = new JWebBrowser();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
