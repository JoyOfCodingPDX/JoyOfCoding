package edu.pdx.cs410G.swing;

import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

/**
 * This program demonstrates Swing's facilities for a Multiple
 * Document Interface (MDI) using a {@link JDesktopPane} and multiple
 * {@link JInternalFrame}s.
 */
public class MDIExample extends JFrame {

  /** The desktop pane that holds all of the JInternalFrames */
  private JDesktopPane desktop;

  /** The windows menu for selecting an internal pane */
  private JMenu windows;

  //////////////////////  Constructors  ///////////////////////

  /**
   * Create a {@link JFrame} that contains a {@link JDesktopPane}.
   * Let the user populate with multiple {@link JInternalFrame}s.
   */
  public MDIExample() {
    super("MDI Example");

    // The content pane of this JFrame is a JDesktopPane
    this.desktop = new JDesktopPane();
    this.setContentPane(this.desktop);

    // Add a menu item that opens a file and displays it in
    // JInternalFrame
    JMenuBar menuBar = new JMenuBar();

    JMenu file = new JMenu("File");
    file.add(new JMenuItem(new OpenAction()));
    file.add(new JMenuItem(new CloseAction()));
    menuBar.add(file);

    this.windows = new JMenu("Windows");
    menuBar.add(windows);

    this.setJMenuBar(menuBar);

    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  ///////////////////////  Main Program  ////////////////////////

  /**
   * Starts up an <code>MDIExample</code>
   */
  public static void main(String[] args) {
    MDIExample mdi = new MDIExample();
    mdi.setSize(500, 500);
    mdi.setVisible(true);
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
        ex.printStackTrace();
	return;
      }
      
      JEditorPane editor = new JEditorPane();
      editor.setText(text);

      // Create a JInternalFrame that displays the text
      JInternalFrame internal =
        new JInternalFrame(file.getName(), true /* resizable */,
                           true /* closable */);
      internal.getContentPane().add(new JScrollPane(editor));
      internal.setDefaultCloseOperation(internal.DISPOSE_ON_CLOSE);
      internal.pack();

      // Add the JInternalFrame to the JDesktopPane
      MDIExample.this.desktop.add(internal);

      final JMenuItem item = new InternalFrameMenuItem(internal);
      MDIExample.this.windows.add(item);

      internal.addInternalFrameListener(new InternalFrameAdapter() {
          public void internalFrameActivated(InternalFrameEvent e) {
            item.setSelected(true);
          }

          public void internalFrameDeactivated(InternalFrameEvent e) {
            item.setSelected(false);
          }

          public void internalFrameClosed(InternalFrameEvent e) {
            MDIExample.this.windows.remove(item);
          }
        });

      internal.setVisible(true);
    }
  }

  /**
   * An action for closing the currently selected file.
   *
   * @since Summer 2005
   */
  class CloseAction extends AbstractAction {
    
    public CloseAction() {
      super("Close");
    }

    public void actionPerformed(ActionEvent e) {
      JInternalFrame frame = desktop.getSelectedFrame();
      if (frame != null) {
        frame.dispose();
      }
    }
  }

  /**
   * A menu item that, when selected, selects a
   * <code>JInternalFrame</code>. 
   *
   * @since Summer 2005
   */
  private class InternalFrameMenuItem extends JCheckBoxMenuItem {

    /**
     * Creates a new <code>InternalFrameMenuItem</code> for the given
     * <code>JInternalFrame</code>
     */
    InternalFrameMenuItem(final JInternalFrame frame) {
      this.setAction(new AbstractAction(frame.getTitle()) {
          public void actionPerformed(ActionEvent e) {
            try {
              desktop.getSelectedFrame().setSelected(false);
              desktop.setSelectedFrame(frame);
              frame.setSelected(true);

            } catch (java.beans.PropertyVetoException ex) {
              ex.printStackTrace(System.err);
            }
          }
        });
    }

  }

}
