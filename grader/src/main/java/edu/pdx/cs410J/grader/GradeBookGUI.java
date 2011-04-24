package edu.pdx.cs410J.grader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import edu.pdx.cs410J.ParserException;

/**
 * This class is a main GUI for manipulate the grade book for CS410J.
 *
 * @author David Whitlock
 * @version $Revision: 1.15 $
 * @since Fall 2000
 */
@SuppressWarnings("serial")
public class GradeBookGUI extends JFrame {

  /** The prefix for the "recent file" preference */
  private static final String RECENT_FILE = "GradeBook_recent_file_";

  /** The maximum number of recent files */
  private static final int MAX_RECENT_FILES = 4;

  ///////////////////////  Instance Fields  ///////////////////////

  private GradeBook book;
  private GradeBookPanel bookPanel;
  private File file;       // Where the grade book lives

  /** The most recently visited files (by name) */
  private ArrayList<String> recentFiles;

  /** Preferences for using this application */
  private Preferences prefs;

  /** A menu listing the recently visited gradebook files */
  private JMenu recentFilesMenu;

  /** A label that displays status messages */
  private JLabel status;

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

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Create and lay out a new <code>GradeBookGUI</code>
   */
  public GradeBookGUI(String title) {
    super(title);

    // Read preference information
    this.prefs = Preferences.userNodeForPackage(this.getClass());
    this.recentFiles = new ArrayList<String>();
    for (int i = 0; i < MAX_RECENT_FILES; i++) {
      String recent = this.prefs.get(RECENT_FILE + i, null);
      if (recent != null) {
        File file = new File(recent);
        if (file.exists() && file.isFile()) {
          this.recentFiles.add(0, recent);
        }
      }
    }
    
    // Set up the actions
    this.setupActions();

    // Add the menus
    JMenuBar menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    this.addFileMenu(menuBar);
    this.updateRecentFilesMenu();

    this.getContentPane().setLayout(new BorderLayout());

    // Add the tool bar
    this.getContentPane().add(this.createToolBar(),
			      BorderLayout.NORTH);

    // Add the GradeBookPanel
    this.bookPanel = new GradeBookPanel(this);
    this.getContentPane().add(this.bookPanel, BorderLayout.CENTER);

    // Add the status label.  Status messages are displaed for five
    // seconds
    this.status = new JLabel(" ") {
        private java.util.Timer timer =
          new java.util.Timer(true /* isDaemon */);
        private TimerTask lastTask = null;

        public void setText(String text) {
          super.setText(text);
          if ("".equals(text.trim())) {
            // Avoid recusion
            return;
          }

          if (lastTask != null) {
            lastTask.cancel();
          }
          lastTask = new TimerTask() {
              public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      // Empty label looks funny with border
                      GradeBookGUI.this.status.setText(" ");
                    }
                  });
              }
            };
          timer.schedule(lastTask, 5 * 1000);
        }
      };
    Border inside = BorderFactory.createEmptyBorder(0, 2, 0, 0);
    Border outside = BorderFactory.createLoweredBevelBorder();
    Border border =
      BorderFactory.createCompoundBorder(outside, inside); 
    this.status.setBorder(border);
    this.getContentPane().add(this.status, BorderLayout.SOUTH);

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

    this.recentFilesMenu = new JMenu("Recent Files");
    this.recentFilesMenu.setMnemonic(KeyEvent.VK_R);
    fileMenu.add(this.recentFilesMenu);

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
   * Makes note that the current <code>file</code> has been changed.
   * Updates the preferences accordingly.
   *
   * @since Fall 2004
   */
  private void noteRecentFile() {
    assert this.file != null;
    String name = this.file.getAbsolutePath();
    this.recentFiles.remove(name);
    this.recentFiles.add(0, name);
    while (this.recentFiles.size() > MAX_RECENT_FILES) {
      this.recentFiles.remove(this.recentFiles.size() - 1);
    }
    for (int i = 0;
         i < this.recentFiles.size() && i < MAX_RECENT_FILES;
         i++) {
      this.prefs.put(RECENT_FILE + i,
                     (String) this.recentFiles.get(i));
    }
    updateRecentFilesMenu();
  }

  /**
   * Updates the contents of the "recent files" menu
   *
   * @since Fall 2004
   */
  private void updateRecentFilesMenu() {
    this.recentFilesMenu.removeAll();
    JMenuItem clear = new JMenuItem("Clear recent files");
    clear.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          GradeBookGUI.this.recentFiles.clear();
          updateRecentFilesMenu();
        }
      });
    this.recentFilesMenu.add(clear);
    this.recentFilesMenu.addSeparator();

    for (int i = 0; i < this.recentFiles.size(); i++) {
      String fileName = (String) this.recentFiles.get(i);
      final File file = new File(fileName);
      if (file.exists()) {
        JMenuItem item = new JMenuItem((i+1) + ": " + fileName);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              open(file);
            }
          });
        this.recentFilesMenu.add(item);
      }
    }
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
      this.status.setText("Saved " + this.file);

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
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
      noteRecentFile();
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
      open(file);
    }
  }

  /**
   * Opens the grade book stored in the given file
   */ 
  private void open(File file) {
    this.checkDirtyGradeBook();

    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(file);
      GradeBook book = parser.parse();
      this.displayGradeBook(book);
      this.file = file;
      noteRecentFile();

    } catch (IOException ex) {
      error("While parsing file " + file.getName(), ex);
      return;

    } catch (ParserException ex) {
      error("While parsing file " + file.getName(), ex);
    }

    this.status.setText("Opened " + file);
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
          this.status.setText("Imported " + student.getFullName());
        
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
    this.status.setText("Created new graded book for \"" + className +
                        "\"");
  }

  /**
   * Prompts the user to save modified data and exits
   */
  private void exit() {
    checkDirtyGradeBook();
    System.exit(0);
  }

  /**
   * If the grade book has been modified and not saved, ask the user
   * if he wants to save it.
   */
  private void checkDirtyGradeBook() {
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
    GradeBookGUI gui = new GradeBookGUI("CS410J Grade Book Program");

    if (args.length > 0) {
      PrintStream err = System.err;
      File file = new File(args[0]);
      if (!file.exists()) {
        err.println("** " + file + " does not exist");

      } else if (!file.isFile()) {
        err.println("** " + file + " is not a file");

      } else {
        gui.open(file);
      }
    }

    gui.pack();
    gui.setVisible(true);
  }

}
