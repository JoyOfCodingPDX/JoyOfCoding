package edu.pdx.cs410J.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * This application shows off a number of Swing's basic features by
 * allowing the user to graphically edit a text file.
 *
 * <P align="center"><EM><A href =
 * "{@docRoot}/../src/edu/pdx/cs410J/gui/TextEditor.java">
 * View Source</A></EM></P>
 */
public class TextEditor extends JFrame {
  private File file;    // The File being edited
  private final JEditorPane editorPane = new JEditorPane();
  private boolean verifyOnExit = true;

  // Text for tool tips
  private static final String FILEMENU_TIP = 
    "Performs operations on the file";
  private static final String OPEN_TIP = 
    "Opens a text file for editing";
  private static final String SAVE_TIP = 
    "Saves the text to a file";
  private static final String VERIFY_TIP = 
    "Ask use before exiting";
  private static final String EXIT_TIP = "Exits the program";

  /**
   * Constructor that sets up all of the components of the
   * <code>TextEditor</code>.
   */
  public TextEditor(String title) {
    super(title);

    // Add the menus 
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    this.addFileMenu(menuBar);
    this.addEditMenu(menuBar);
    this.addPlafMenu(menuBar);



    // Set up the editorPane
    editorPane.setEditable(true);
    this.newTextFile();

    // Set up a JScrollPane around the editorPane
    JScrollPane scrollPane = new JScrollPane(editorPane);
    scrollPane.setVerticalScrollBarPolicy(
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setPreferredSize(new Dimension(250, 145));
    scrollPane.setMinimumSize(new Dimension(100, 100));
    

    // Add the scroll pane to the content pane
    JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());

    // Adding it to the center gets the resizing right
    contentPane.add(scrollPane, BorderLayout.CENTER);
    this.setContentPane(contentPane);

    // Handle exit events
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          checkExit();
        }
      });
  }

  /**
   * Creates a File menu.
   */
  private void addFileMenu(JMenuBar menuBar) {
    // File menu for dealing with files
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.getAccessibleContext().setAccessibleDescription(FILEMENU_TIP);
    menuBar.add(fileMenu);
                                                            
    JMenuItem newItem = new JMenuItem("New");
    newItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newTextFile();
        }
      });
    fileMenu.add(newItem);

    JMenuItem openItem = new JMenuItem("Open...");
    openItem.getAccessibleContext().setAccessibleDescription(OPEN_TIP);
    openItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          loadTextFile();
        }
      });
    fileMenu.add(openItem);

    JMenuItem saveAsItem = new JMenuItem("Save As...");
    saveAsItem.getAccessibleContext().setAccessibleDescription(SAVE_TIP);
    saveAsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveAs();
        }
      });
    fileMenu.add(saveAsItem);

    JMenuItem saveItem = new JMenuItem("Save...");
    saveItem.getAccessibleContext().setAccessibleDescription(SAVE_TIP);
    saveItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveTextFile();
        }
      });
    fileMenu.add(saveItem);

    fileMenu.addSeparator();

    JCheckBoxMenuItem verifyItem = 
      new JCheckBoxMenuItem("Confirm before exiting");
    verifyItem.setState(this.verifyOnExit);
    verifyItem.getAccessibleContext().setAccessibleDescription(VERIFY_TIP);
    verifyItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
          boolean state = item.getState();
          // State has already been toggled!
          verifyOnExit = state;
          item.setState(state);
        }
      });
    fileMenu.add(verifyItem);

    fileMenu.addSeparator();

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.getAccessibleContext().setAccessibleDescription(EXIT_TIP);
    exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          checkExit();
        }
      });
    fileMenu.add(exitItem);
  }

  /**
   * Creates an Edit menu.
   */
  private void addEditMenu(JMenuBar menuBar) {
    JMenu editMenu = new JMenu("Edit");
    editMenu.setMnemonic(KeyEvent.VK_E);
    menuBar.add(editMenu);

    JMenuItem cutItem = new JMenuItem("Cut");
    cutItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editorPane.cut();
        }
      });
    editMenu.add(cutItem);

    JMenuItem copyItem = new JMenuItem("Copy");
    copyItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editorPane.copy();
        }
      });
    editMenu.add(copyItem);

    JMenuItem pasteItem = new JMenuItem("Paste");
    pasteItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editorPane.paste();
        }
      });
    editMenu.add(pasteItem);    
  }

  /**
   * Creates menu that allows the user to change the look and feel of
   * this <code>TextEditor</code>.
   */
  private void addPlafMenu(JMenuBar menuBar) {
    JMenu plafMenu = new JMenu("Look & Feel");
    plafMenu.setMnemonic(KeyEvent.VK_L);
    menuBar.add(plafMenu);

    UIManager.LookAndFeelInfo[] infos =
      UIManager.getInstalledLookAndFeels();
    for (int i = 0; i < infos.length; i++) {
      final UIManager.LookAndFeelInfo info = infos[i];

      JMenuItem plafItem = new JMenuItem(info.getName());
      plafItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              UIManager.setLookAndFeel(info.getClassName());
              SwingUtilities.updateComponentTreeUI(TextEditor.this);
              TextEditor.this.pack();

            } catch (Exception ex) {
              error(ex.toString());
              return;
            }
          }
        });
      plafMenu.add(plafItem);
    }
  }

  /**
   * Pops up a dialog box that notifies the user of a non-fatal
   * error. 
   */
  private void error(String message) {
    JOptionPane.showMessageDialog(this, new String[] {
      "A non-fatal error has occurred.", 
      message},
                                   "Non-fatal error",
                                   JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Clears the editorPane and resets the file
   */
  private void newTextFile() {
    this.file = null;
    this.editorPane.setText("");
  }

  /**
   * Loads the contents of a text file into the editorPane.
   */
  private void loadTextFile() {
    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Open text file");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    int response = chooser.showOpenDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      // Read in the file and display its text to the editorPane
      this.file = chooser.getSelectedFile();
      StringBuffer sb = new StringBuffer();

      try {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        char[] buffer = new char[1024];
        while (br.ready()) {
          int count = br.read(buffer, 0, buffer.length);
          sb.append(buffer, 0, count);
        }

      } catch (IOException ex) {
        error(ex.toString());
        return;
      }

      this.editorPane.setText(sb.toString());
    }
  }

  /**
   * Creates a <code>JFileChooser</code> suitable for dealing with
   * text files.
   */
  private JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    if (this.file != null) {
      chooser.setCurrentDirectory(file.getParentFile());
    }

    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File file) {
          if (file.isDirectory()) {
            return true;
          }

          String fileName = file.getName();
          return fileName.endsWith(".txt");
        }

        public String getDescription() {
          return "Text files";
        }
      });
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setMultiSelectionEnabled(false);

    return chooser;
  }

  /**
   * Saves the contents of the editorPane to a text file.
   */
  private void saveTextFile() {
    if (this.file == null) {
      saveAs();
      return;
    }

    try{
      FileWriter fw = new FileWriter(this.file);
      PrintWriter pw = new PrintWriter(fw);
      pw.print(this.editorPane.getText());
      pw.flush();

    } catch (IOException ex) {
      error(ex.toString());
      return;
    }
  }

  /**
   * Pops up a "Save as" dialog box.
   */
  private void saveAs() {
    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Save as...");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    int response = chooser.showSaveDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      this.file = chooser.getSelectedFile();
      saveTextFile();
    }
  }

  /**
   * Called when the <code>TextEditor</code> is about to exit.  Pop up
   * a verifcation dialog box.
   */
  private void checkExit() {
    if (this.verifyOnExit) {
      int response = JOptionPane.showConfirmDialog(this, new String[] {
        "You are about to exit.",
        "Are you sure you want to do this?"},
        "Confirm Exit",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE);

      if (response == JOptionPane.YES_OPTION) {
        System.exit(0);
      }

    } else {
      System.exit(0);
    }
  }

  /**
   * Starts up a <code>TextEditor</code>.
   */
  public static void main(String[] args) {
    TextEditor edit = new TextEditor("CS410J Text Editor");

    edit.pack();
    edit.setVisible(true);
  }

}
