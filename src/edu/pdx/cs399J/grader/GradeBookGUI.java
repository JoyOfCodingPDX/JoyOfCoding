package edu.pdx.cs399J.grader;

import edu.pdx.cs399J.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
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

  /** An action for creating a new grade book file */
  private Action newAction;

  /** An action for importing students into a gradebook */
  private Action importAction;

  /** An action for opening a gradebook */
  private Action openAction;

  /** An action for saving a gradebook */
  private Action saveAction;

  /** An action for saving a gradebook with a given file name */
  private Action saveAsAction;

  /** An action for exiting the GUI */
  private Action exitAction;

  /**
   * Create and lay out a new <code>GradeBookGUI</code>
   */
  public GradeBookGUI(String title) {
    super(title);

    // Set up the actions
    this.setupActions();

    // Add the menus
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    this.addFileMenu(menuBar);

    this.getContentPane().setLayout(new BorderLayout());

    // Add the tool bar
    this.getContentPane().add(this.createToolBar(),
			      BorderLayout.NORTH);

    // Add the GradeBookPanel
    this.bookPanel = new GradeBookPanel(this);
    this.getContentPane().add(this.bookPanel, BorderLayout.CENTER);

    // Handle exit events
    this.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          exit();
        }
      });
  }

  /**
   * Initializes the action objects that encapsulate actions that can
   * be performed by various widgets in this GUI.
   */
  private void setupActions() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    // New action
    this.newAction = new AbstractAction("New ...") {
	public void actionPerformed(ActionEvent e) {
	  newGradeBook();
	}
      };
    this.newAction.putValue(Action.SHORT_DESCRIPTION,
			       "New grade book");
    this.newAction.putValue(Action.LONG_DESCRIPTION,
			       "Create a new grade book");
    this.newAction.putValue(Action.ACCELERATOR_KEY, 
			       KeyStroke.getKeyStroke("control N"));
    this.newAction.putValue(Action.MNEMONIC_KEY, 
			       new Integer(KeyEvent.VK_N));
    URL newURL =
      cl.getResource("toolbarButtonGraphics/general/New16.gif");
    if (newURL != null) {
      this.newAction.putValue(Action.SMALL_ICON, new ImageIcon(newURL));
    }

    // Open action
    this.openAction = new AbstractAction("Open ...") {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      };
    this.openAction.putValue(Action.SHORT_DESCRIPTION,
			       "Open grade book");
    this.openAction.putValue(Action.LONG_DESCRIPTION,
			       "Open a grade book file");
    this.openAction.putValue(Action.ACCELERATOR_KEY, 
			       KeyStroke.getKeyStroke("control O"));
    this.openAction.putValue(Action.MNEMONIC_KEY, 
			       new Integer(KeyEvent.VK_O));
    URL openURL =
      cl.getResource("toolbarButtonGraphics/general/Open16.gif");
    if (openURL != null) {
      this.openAction.putValue(Action.SMALL_ICON, new ImageIcon(openURL));
    }

    // Import action
    this.importAction = new AbstractAction("Import...") {
	public void actionPerformed(ActionEvent e) {
	  importStudent();
	}
      };
    this.importAction.putValue(Action.SHORT_DESCRIPTION,
			       "Import students");
    this.importAction.putValue(Action.LONG_DESCRIPTION,
			       "Import students into a grade book");
    this.importAction.putValue(Action.ACCELERATOR_KEY, 
			       KeyStroke.getKeyStroke("control I"));
    this.importAction.putValue(Action.MNEMONIC_KEY, 
			       new Integer(KeyEvent.VK_I));
    this.importAction.setEnabled(false);
    URL importURL =
      cl.getResource("toolbarButtonGraphics/general/Import16.gif");
    if (importURL != null) {
      this.importAction.putValue(Action.SMALL_ICON, new ImageIcon(importURL));
    }

    // Save action
    this.saveAction = new AbstractAction("Save") {
	public void actionPerformed(ActionEvent e) {
	  save();
	}
      };
    this.saveAction.putValue(Action.SHORT_DESCRIPTION,
			       "Save grade book");
    this.saveAction.putValue(Action.LONG_DESCRIPTION,
			       "Save the current grade book file");
    this.saveAction.putValue(Action.ACCELERATOR_KEY, 
			       KeyStroke.getKeyStroke("control S"));
    this.saveAction.putValue(Action.MNEMONIC_KEY, 
			       new Integer(KeyEvent.VK_S));
    this.saveAction.setEnabled(false);
    URL saveURL =
      cl.getResource("toolbarButtonGraphics/general/Save16.gif");
    if (saveURL != null) {
      this.saveAction.putValue(Action.SMALL_ICON, new ImageIcon(saveURL));
    }

    // Save As action
    this.saveAsAction = new AbstractAction("Save As ...") {
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      };
    this.saveAsAction.putValue(Action.SHORT_DESCRIPTION,
			       "Save grade book");
    this.saveAsAction.putValue(Action.MNEMONIC_KEY, 
			       new Integer(KeyEvent.VK_A));
    this.saveAsAction.setEnabled(false);
    URL saveAsURL =
      cl.getResource("toolbarButtonGraphics/general/SaveAs16.gif");
    if (saveAsURL != null) {
      this.saveAsAction.putValue(Action.SMALL_ICON, new ImageIcon(saveAsURL));
    }

    // Exit Action
    this.exitAction = new AbstractAction("Exit") {
	public void actionPerformed(ActionEvent e) {
	  exit();
	}
      };
    this.exitAction.putValue(Action.SHORT_DESCRIPTION, "Exit");
    this.exitAction.putValue(Action.LONG_DESCRIPTION,
			     "Exit the grade book program");
    this.exitAction.putValue(Action.ACCELERATOR_KEY, 
			     KeyStroke.getKeyStroke("control Q"));
    this.exitAction.putValue(Action.MNEMONIC_KEY, 
			     new Integer(KeyEvent.VK_X));
  }

  /**
   * Returns a <code>JToolBar</code> for this GUI
   */
  private JToolBar createToolBar() {
    JToolBar tools = new JToolBar(JToolBar.HORIZONTAL);
    tools.setFloatable(false);
    tools.add(this.newAction);
    tools.add(this.openAction);
    tools.add(this.importAction);
    tools.addSeparator();
    tools.add(this.saveAction);
    tools.add(this.saveAsAction);
    return tools;
  }

  /**
   * Adds a "File" menu to this gui
   */
  private void addFileMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);

    fileMenu.add(new JMenuItem(this.newAction));
    fileMenu.add(new JMenuItem(this.openAction));
    fileMenu.add(new JMenuItem(this.importAction));

    fileMenu.addSeparator();

    fileMenu.add(new JMenuItem(this.saveAction));
    fileMenu.add(new JMenuItem(this.saveAsAction));

    fileMenu.addSeparator();

    fileMenu.add(new JMenuItem(this.exitAction));
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
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
    int response = chooser.showOpenDialog(this);
    
    if (response == JFileChooser.APPROVE_OPTION) {
      // Read the student from each of the selected files and add it
      // to the grade book
      File[] files = chooser.getSelectedFiles();
      for (int i = 0; i < files.length; i++) {
	File file = files[i];
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
    this.importAction.setEnabled(true);
    this.saveAction.setEnabled(true);
    this.saveAsAction.setEnabled(true);
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
