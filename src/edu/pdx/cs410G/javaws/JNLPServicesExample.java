package edu.pdx.cs410G.javaws;

import java.awt.BorderLayout;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;

/**
 * This program demonstrates a number of JNLP's services.  Note that
 * this program must be run by a JNLP Client such as Java Web Start.
 */
public class JNLPServicesExample extends JPanel {

  /**
   * Create a {@link JTabbedPane} with various examples of JNLP
   * services 
   */
  public JNLPServicesExample() {
    this.setLayout(new BorderLayout());
    JTabbedPane tabs = new JTabbedPane();
    this.add(tabs);

    String title = "BasicService";
    try {
      tabs.add(title, new BasicServiceExample());

    } catch (UnavailableServiceException ex) {
      tabs.add(title, new JLabel("BasicService not available"));
    }
  }

  /**
   * Main program that displays the example of JNLP services. 
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Examples of JNLP Services");
    frame.getContentPane().add(new JNLPServicesExample());
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

}
