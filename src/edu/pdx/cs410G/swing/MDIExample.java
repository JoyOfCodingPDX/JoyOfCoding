package edu.pdx.cs410G.swing;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

/**
 * This program demonstrates Swing's facilities for a Multiple
 * Document Interface (MDI) using a {@link JDesktopPane} and multiple
 * {@link JInternalPane}s.
 */
public class MDIExample extends JFrame {

  /**
   * Create a {@link JFrame} that contains a {@link JDesktopPane}.
   * Let the user populate with multiple {@link JInternalPanes}s.
   */
  public MDIExample() {
    super("MDI Example");

    // The content pane of this JFrame is a JDesktopPane
    this.setContentPane(new JDesktopPane());

    // Add a menu item that opens a file and displays it in
    // JInternalFrame
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(new JMenuItem(new OpenAction()));
  }

  ///////////////////////  Inner Classes  ///////////////////////

  /**
   * An action for opening a file.
   */
  class OpenAction extends AbstractAction {
    
    public OpenAction() {
      super("Open");
    }

    public void actionPerformed(ActionEvent e) {
      // Display a JFileChooser
      JFileChooser chooser = new JFileChooser();
      chooser.setFileFilter(new FileFilter() {
	  public boolean accept(File f) {
	    return f.getName().endsWith(".txt");
	  }

	  public String getDescription() {
	    return "Text files";
	  }
	});
      int status = chooser.showOpenDialog(MDIExample.this);
      if (status != JFileChooser.APPROVE_OPTION) {
	return;
      }
      File file = chooser.getSelectedFile();
      String text;
      try {
	StringWriter sw = new StringWriter();
	BufferedReader br = new BufferedReader(new FileReader(file));
	while (br.ready()) {
	  String line = br.readLine();
	  sw.write(line);
	  sw.write('\n');
	}
	text = sw.toString();

      } catch (IOException ex) {
	return;
      }
      
      // Create a JInternalFrame that displays the text
      JInternalFrame internal = new JInternalFrame();
      JEditorPane editor = new JEditorPane();
      editor.setText(text);
      internal.getContentPane().add(new JScrollPane(editor));

      // Add the JInternalFrame to the JDesktopPane
      JDesktopPane desktop = 
	(JDesktopPane) MDIExample.this.getContentPane();
      desktop.add(internal);
    }
  }

  ///////////////////////  Main Program  ////////////////////////

  
}
