package edu.pdx.cs410G.swing;

import java.io.*;
import javax.swing.*;

/**
 * This program demonstrates a {@link JScrollPane} with a customer
 * {@link JScrollPane#setRowHeaderView row header}.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 */
public class JScrollPaneExample extends JFrame {

  /**
   * Creates a new <code>JScrollPaneExample</code> that displays the
   * contents of the given <code>File</code>
   */
  public JScrollPaneExample(File file) {
    super("JScrollPaneExample: " + file.getName());
  }

}
