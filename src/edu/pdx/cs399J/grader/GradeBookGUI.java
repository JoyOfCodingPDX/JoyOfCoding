package edu.pdx.cs399J.grader;

import edu.pdx.cs399J.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class is a main GUI for manipulate the grade book for CS399J.
 */
public class GradeBookGUI extends JFrame {

  private GradeBook book;
  private GradeBookPanel bookPanel;
  private File file;       // Where the grade book lives

  /**
   * Create and lay out a new <code>GradeBookGUI</code>
   */
  public GradeBookGUI(String title) {
    super(title);

    // Add the menus
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    this.addFileMenu(menuBar);

    // Add the GradeBookPanel
    this.bookPanel = new GradeBookPanel(this);
    this.getContentPane().add(this.bookPanel);

    // Handle exit events
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          exit();
        }
      });
  }

  /**
   * Adds a "File" menu to this gui
   */
  private void addFileMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);

    JMenuItem newItem = new JMenuItem("New");
    newItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newGradeBook();
        }
      });
    fileMenu.add(newItem);

    JMenuItem openItem = new JMenuItem("Open...");
    openItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          open();
        }
      });
    fileMenu.add(openItem);

    JMenuItem importItem = new JMenuItem("Import...");
    importItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          importStudent();
        }
      });
    fileMenu.add(importItem);

    fileMenu.addSeparator();

    JMenuItem saveAsItem = new JMenuItem("Save As...");
    saveAsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveAs();
        }
      });
    fileMenu.add(saveAsItem);

    JMenuItem saveItem = new JMenuItem("Save...");
    saveItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          save();
        }
      });
    fileMenu.add(saveItem);

    fileMenu.addSeparator();

    JMenuItem exitItem = new JMenuItem("Exit");
    exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exit();
        }
      });
    fileMenu.add(exitItem);
  }

  /**
   * Displays a dialog box alerting the user that an error has
   * occurred.
   */
  private void error(String message) {
    JOptionPane.showMessageDialog(this, message,
                                  "An error occurred",
                                  JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Displays a dialog box alerting the user that an exception was
   * thrown
   */
  private void error(String message, Exception ex) {
    JOptionPane.showMessageDialog(this, new String[] {
      message, ex.getMessage()},
                                  "An exception was thrown",
                                  JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Saves the grade book being edited to a file
   */
  private void save() {
    if (this.book == null) {
      // Nothing to do
      return;
    }

    if (this.file == null) {
      saveAs();
      // saveAs() will recursively invoke save() if the user selects a
      // file
      return;
    }

    // Dump grade to file as XML
    try {
      XmlDumper dumper = new XmlDumper(this.file);
      dumper.dump(this.book);

    } catch (IOException ex) {
      error("While writing grade book", ex);
      return;
    }
  }

  /**
   * Creates a <code>JFileChooser</code> suitable for dealing with
   * xml files.
   */
  private JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    if (this.file != null) {
      chooser.setCurrentDirectory(file.getAbsoluteFile().getParentFile());
    } else {
      String cwd = System.getProperty("user.dir");
      chooser.setCurrentDirectory(new File(cwd));
    }

    chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
        public boolean accept(File file) {
          if (file.isDirectory()) {
            return true;
          }

          String fileName = file.getName();
          return fileName.endsWith(".xml");
        }

        public String getDescription() {
          return "XML files";
        }
      });
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setMultiSelectionEnabled(false);

    return chooser;
  }

  /**
   * Prompts the user for a file to save the grade book in.
   */
  private void saveAs() {
    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Save as...");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    int response = chooser.showSaveDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      this.file = chooser.getSelectedFile();
      save();
    }
  }

  /**
   * Prompts the user for a file to display
   */
  private void open() {
    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Open grade book file");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    int response = chooser.showOpenDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      // Read the grade book from the file and display it in the GUI
      File file = chooser.getSelectedFile();
      
      try {
        XmlGradeBookParser parser = new XmlGradeBookParser(file);
        GradeBook book = parser.parse();
        this.displayGradeBook(book);
        this.file = file;

      } catch (IOException ex) {
        error("While parsing file " + file.getName(), ex);
        return;

      } catch (ParserException ex) {
        error("While parsing file " + file.getName(), ex);
      }
    }
  }

  /**
   * Prompts the user for a file from which a student is read and
   * added to the grade book
   */
  private void importStudent() {
    if (this.book == null) {
      error("You must open a grade book before importing a student");
      return;
    }
    
    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Import student XML file");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    int response = chooser.showOpenDialog(this);
    
    if (response == JFileChooser.APPROVE_OPTION) {
      // Read the student from the selected file and add it to the
      // grade book
      File file = chooser.getSelectedFile();
      try {
        XmlStudentParser sp = new XmlStudentParser(file);
        Student student = sp.parseStudent();
        this.book.addStudent(student);
        
      } catch (IOException ex) {
        error("Could not access " + file.getName(), ex);
        return;

      } catch (ParserException ex) {
        error("While parsing file " + file.getName(), ex);
        return;
      }
    }
  }

  /**
   * Creates a new grade book to be edited
   */
  private void newGradeBook() {
    // Prompt user for name of grade book
    String className = 
      JOptionPane.showInputDialog(this, 
                                  "Enter the name of the class",
                                  "Enter class name",
                                  JOptionPane.INFORMATION_MESSAGE);

    if (className == null || className.equals("")) {
      return;
    }

    this.displayGradeBook(new GradeBook(className));
    this.file = null;
  }

  /**
   * Called when the GUI is about to exit.  If the grade book has been
   * modified and not saved, ask the user if he wants to save it.
   */
  private void exit() {
    if (this.book != null && this.book.isDirty()) {
      int response = JOptionPane.showConfirmDialog(this, new String[] {
        "You have made changes to the grade book.",
        "Do you want to save them?"},
        "Save Changes?",
        JOptionPane.YES_NO_CANCEL_OPTION,
        JOptionPane.QUESTION_MESSAGE);
      
      if (response == JOptionPane.YES_OPTION) {
        save();

      } else if (response == JOptionPane.CANCEL_OPTION) {
        // Don't exit
        return;
      }
    }

    System.exit(0);
  }

  /**
   * Displays a <code>GradeBook</code> in this GUI
   */
  private void displayGradeBook(GradeBook book) {
    this.book = book;
    this.setTitle(book.getClassName());
    this.bookPanel.displayGradeBook(this.book);
  }

  /**
   * Main program.  Create and show a <code>GradeBookGUI</code>
   */
  public static void main(String[] args) {
    GradeBookGUI gui = new GradeBookGUI("CS399J Grade Book Program");

    if (args.length > 0) {
      PrintStream err = System.err;
      File file = new File(args[0]);
      if (!file.exists()) {
        err.println("** " + file + " does not exist");

      } else if (!file.isFile()) {
        err.println("** " + file + " is not a file");

      } else {
        try {
          XmlGradeBookParser parser = new XmlGradeBookParser(file);
          GradeBook book = parser.parse();
          gui.displayGradeBook(book);
          gui.file = file;

        } catch (IOException ex) {
          err.println("While parsing file " + file.getName() + 
                      ": " + ex);
          System.exit(1);

        } catch (ParserException ex) {
          err.println("While parsing file " + file.getName() +
                      ": " + ex);
          System.exit(1);
        }
      }
    }

    gui.pack();
    gui.setVisible(true);
  }

}
