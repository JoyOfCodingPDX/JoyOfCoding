package edu.pdx.cs410G.javaws;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import javax.jnlp.*;
import javax.swing.*;

/**
 * This class shows off the capabilities of JNLP's {@link
 * FileOpenService}, {@link FileSaveService}, and {@link FileContents}
 * classes.
 */
public class FileServicesExample extends JPanel {

  /** The name of the file being edited */
  private String fileName;

  /**
   * Builds a GUI that allows the user to edit a text file.
   */
  public FileServicesExample() throws UnavailableServiceException {
    this.setLayout(new BorderLayout());
    
    final FileOpenService openService =
      (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
    final FileSaveService saveService =
      (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");

    final JLabel label = new JLabel();
    this.add(BorderLayout.SOUTH, label);

    final JEditorPane editor = new JEditorPane();
    this.add(BorderLayout.CENTER, new JScrollPane(editor));

    JPanel p = new JPanel();    

    JButton open = new JButton("Open");
    open.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String[] exts = new String[] { "txt", "text" };
	  String dir = "";
	  try {
	    FileContents file =
	      openService.openFileDialog(dir, exts);
	    InputStream is = file.getInputStream();
	    editor.read(is, null);
	    fileName = file.getName();
	    label.setText(fileName);

	  } catch (IOException ex) {
	    ex.printStackTrace(System.err);
	    return;
	  }
	}
      });
    p.add(open);

    JButton save = new JButton("Save");
    save.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  if (fileName == null) {
	    return;
	  }

	  String[] exts = new String[] { "txt", "text" };
	  String dir = "";
	  String text = editor.getText();

	  try {
	    InputStream is =
	      new ByteArrayInputStream(text.getBytes());
	    FileContents file =
	      saveService.saveFileDialog(dir, exts, is, fileName);

	  } catch (IOException ex) {
	    ex.printStackTrace(System.err);
	    return;
	  }
	}
      });
    p.add(save);
    

    this.add(BorderLayout.NORTH, p);
  }

}
