package edu.pdx.cs410J.family;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This class is a graphical user interface that lets the user edit a
 * family tree.
 */
@SuppressWarnings("serial")
public class FamilyTreeGUI extends FamilyTreePanel {
  private File file;        // Where FamilyTree lives
  private boolean isDirty = false;  // Has the family tree been modified?

  // GUI components worth holding onto
  private JMenuItem saveItem;       // Disabled until we have a tree
  private JMenuItem saveAsItem;     // Disabled until we have a tree
  private JMenu personMenu;         // Disabled until a person is selected
  private JMenuItem motherItem;
  private JMenuItem fatherItem;
  private JFrame frame;             // Frame containing this GUI

  /**
   * Creates a new <code>FamilyTreeGUI</code>
   */
  public FamilyTreeGUI(String title) {
    // Implicit call to super class's constructor
    super.addComponents();

    // Create a JFrame
    this.frame = new JFrame(title);

    // Add the menus
    JMenuBar menuBar = new JMenuBar();
    this.frame.setJMenuBar(menuBar);
    this.addFileMenu(menuBar);
    this.addPersonMenu(menuBar);
    this.addPlafMenu(menuBar);

    // Add myself to the frame
    System.out.println("Adding this");
    this.frame.getContentPane().add(this);

    // Initially, we're not dirty
    this.setDirty(false);

    // Handle exit events
    this.frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          exit();
        }
      });

  }

  /**
   * Returns <code>true</code> if this GUI can be used to edit a
   * family tree.
   */
  boolean canEdit() {
    return true;
  }

  /**
   * Creates the File menu
   */
  private void addFileMenu(JMenuBar menuBar) {
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);

    JMenuItem openItem = new JMenuItem("Open...", KeyEvent.VK_O);
    openItem.setAccelerator(KeyStroke.getKeyStroke(
                      KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    openItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          open();
        }
      });
    fileMenu.add(openItem);
    
    this.saveItem = new JMenuItem("Save", KeyEvent.VK_S);
    saveItem.setAccelerator(KeyStroke.getKeyStroke(
                      KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    saveItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          save();
        }
      });
    fileMenu.add(saveItem);
    
    this.saveAsItem = new JMenuItem("Save As...", KeyEvent.VK_A);
    saveAsItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveAs();
        }
      });
    fileMenu.add(saveAsItem);

    fileMenu.addSeparator();

    JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
    exitItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exit();
        }
      });
    fileMenu.add(exitItem);
  }

  /**
   * Creates the Person menu
   */
  private void addPersonMenu(JMenuBar menuBar) {
    this.personMenu = new JMenu("Person");
    personMenu.setMnemonic(KeyEvent.VK_P);
    personMenu.setEnabled(false);
    menuBar.add(personMenu);

    fatherItem = new JMenuItem("Father", KeyEvent.VK_F);
    fatherItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          displayFather();
        }
      });
    personMenu.add(fatherItem);
    
    motherItem = new JMenuItem("Mother", KeyEvent.VK_M);
    motherItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          displayMother();
        }
      });
    personMenu.add(motherItem);

    personMenu.addSeparator();

    JMenuItem editItem = new JMenuItem("Edit...", KeyEvent.VK_E);
    editItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editPerson();
        }
      });
    personMenu.add(editItem);
    
    JMenuItem marriageItem = new JMenuItem("Add Marriage...",
                                           KeyEvent.VK_M);
    marriageItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addMarriage();
        }
      });
    personMenu.add(marriageItem);
  }

  /**
   * Creates menu that allows the user to change the look and feel of
   * this <code>FamilyTreeGUI</code>.
   */
  private void addPlafMenu(JMenuBar menuBar) {
    JMenu plafMenu = new JMenu("Look & Feel");
    plafMenu.setMnemonic(KeyEvent.VK_L);
    menuBar.add(plafMenu);

    ButtonGroup bg = new ButtonGroup();

    UIManager.LookAndFeelInfo[] infos =
      UIManager.getInstalledLookAndFeels();
    for (int i = 0; i < infos.length; i++) {
      final UIManager.LookAndFeelInfo info = infos[i];

      JRadioButtonMenuItem plafItem;
      if (info.getName().equals(UIManager.getLookAndFeel().getName()))
        {
          plafItem = new JRadioButtonMenuItem(info.getName(), true);

        } else {
          plafItem = new JRadioButtonMenuItem(info.getName(), false);
        }

      plafItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              UIManager.setLookAndFeel(info.getClassName());
              SwingUtilities.updateComponentTreeUI(FamilyTreeGUI.this);
              FamilyTreeGUI.this.pack();

            } catch (Exception ex) {
              error(ex.toString());
              return;
            }
          }
        });

      bg.add(plafItem);
      plafMenu.add(plafItem);
    }
  }

  /**
   * Pops up a dialog box that notifies the user of a non-fatal
   * error. 
   */
  private void error(String message) {
    JOptionPane.showMessageDialog(this, new String[] {
      message},
                                   "An error has occurred",
                                   JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Brings up a dialog box that edits the current <code>Person</code>
   */
  void editPerson() {
    Person person = this.treeList.getSelectedPerson();
    if (person == null) {
      return;
    }

    EditPersonDialog dialog = 
      new EditPersonDialog(person, this.getFrame(), this.tree);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    person = dialog.getPerson();
    if (person != null) {
      // Assume some change was made
      this.setDirty(true);

      this.showPerson(person);
      this.treeList.fillInList(this.tree);
    }
  }

  /**
   * Sets the <code>Person</code> displayed in the GUI
   */
  void showPerson(Person person) {
    if (person == null) {
      this.personMenu.setEnabled(false);

    } else {
      this.personMenu.setEnabled(true);

      // Can we enable the Mother and Father menus?
      this.motherItem.setEnabled(person.getMother() != null);
      this.fatherItem.setEnabled(person.getFather() != null);
    }

    this.personPanel.showPerson(person);
  }

  /**
   * Brings up a dialog box for editing the a new <code>Person</code>
   */
  void newPerson() {
    EditPersonDialog dialog = 
      new EditPersonDialog(this.getFrame(), this.tree);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);

    Person newPerson = dialog.getPerson();
    if (newPerson != null) {
      // A change was made
      this.setDirty(true);

      this.tree.addPerson(newPerson);
      this.treeList.fillInList(this.tree);
    }
  }

  /**
   * Brings up a dialog box for noting the current
   * <code>Person</code>'s marriage.
   */
  void addMarriage() {
    Person person = this.treeList.getSelectedPerson();

    EditMarriageDialog dialog = 
      new EditMarriageDialog(person, this.getFrame(), this.tree);
    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
   
    Marriage newMarriage = dialog.getMarriage();
    if (newMarriage != null) {
      // A change was made and update person panel
      this.setDirty(true);
      this.showPerson(person);
    }
  }

  /**
   * Saves the family tree to a file
   */
  private void save() {
    if (this.file == null) {
      saveAs();
      return;
    }

//     System.out.println("Saving to an XML file");

    try {
      XmlDumper dumper = new XmlDumper(this.file);
      dumper.dump(this.tree);
      this.setDirty(false);

    } catch (IOException ex) {
      error("Error while saving family tree: " + ex);
    }

  }

  /**
   * Creates a <code>JFileChooser</code> suitable for dealing with
   * text files.
   */
  private JFileChooser getFileChooser() {
    JFileChooser chooser = new JFileChooser();
    if (this.file == null) {
      String cwd = System.getProperty("user.dir");
      chooser.setCurrentDirectory(new File(cwd));

    } else {
      chooser.setCurrentDirectory(this.file.getParentFile());
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
          return "XML files (*.xml)";
        }
      });
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setMultiSelectionEnabled(false);

    return chooser;
  }

  /**
   * Displays a dialog box that allows the user to select an XML file
   * to save the family tree to.
   */
  private void saveAs() {
//     System.out.println("Saving as...");

    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Save As...");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    int response = chooser.showSaveDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      this.file = chooser.getSelectedFile();

      if (this.file.exists()) {
        response = 
          JOptionPane.showConfirmDialog(this, new String[] {
            "File " + this.file + " already exists.",
            "Do you want to overwrite it?"}, 
                                        "Overwrite file?",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.NO_OPTION) {
          // Try it again...
          saveAs();
          return;      // Only save once
        }
      }

      save();
    }
  }

  /**
   * Pops open a dialog box for choosing an XML file
   */
  private void open() {
//     System.out.println("Opening an XML file");

    if (this.isDirty) {
      int response = 
        JOptionPane.showConfirmDialog(this, new String[] {
          "You have made changes to your family tree.",
          "Do you want to save them?"},
                                      "Confirm changes",
                                      JOptionPane.YES_NO_CANCEL_OPTION,
                                      JOptionPane.QUESTION_MESSAGE);
    
      if (response == JOptionPane.YES_OPTION) {
        save();

      } else if (response == JOptionPane.CANCEL_OPTION) {
        // Don't open new file, keep working with old
        return;
      }

      // Otherwise, discard changes and open new file
    }

    JFileChooser chooser = this.getFileChooser();
    chooser.setDialogTitle("Open text file");
    chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    int response = chooser.showOpenDialog(this);

    if (response == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      FamilyTree tree = null;

      try {
        XmlParser parser = new XmlParser(file);
        tree = parser.parse();

      } catch (FileNotFoundException ex) {
        error(ex.toString());

      } catch (FamilyTreeException ex) {
        error(ex.toString());
      }


      if (tree != null) {
        // Everything is okay
        this.file = file;
        this.sourceLocation.setText(this.file.getName());
        this.tree = tree;
        this.setDirty(false);

        this.treeList.fillInList(this.tree);
        this.sourceLocation.setText(this.file.getPath());
      }
    }
  }

  /**
   * Called when the family tree changes dirtiness
   */
  void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
    this.saveAsItem.setEnabled(isDirty);
    this.saveItem.setEnabled(isDirty);
  }

  /**
   * Returns the <code>JFrame</code> associated with this GUI.
   */
  JFrame getFrame() {
    return this.frame;
  }

  /**
   * If the family tree has been modified and not saved, prompt the
   * user to verify that he really wants to exit.
   */
  private void exit() {
    if (this.isDirty) {
      int response = 
        JOptionPane.showConfirmDialog(this, new String[] {
          "You have made changes to your family tree.",
          "Do you want to save them?"},
                                      "Confirm changes",
                                      JOptionPane.YES_NO_CANCEL_OPTION,
                                      JOptionPane.QUESTION_MESSAGE);
    
      if (response == JOptionPane.YES_OPTION) {
        save();
        System.exit(0);

      } else if (response == JOptionPane.NO_OPTION) {
        System.exit(0);
      }

      // Otherwise continue working
      
    } else {
      System.exit(0);
    }
  }

  /**
   * Overridden to pack the containing <code>JFrame</code>.
   */
  public void pack() {
    this.frame.pack();
  }

  /**
   * Overridden to set the visibility of the containing
   * <code>JFrame</code>.
   */
  public void setVisible(boolean isVisible) {
    this.frame.setVisible(isVisible);
  }

  /**
   * Creates a <code>FamilyTreeGUI</code>
   */
  public static void main(String[] args) {
    FamilyTreeGUI gui = new FamilyTreeGUI("Family Tree Program");
    gui.pack();
    gui.setVisible(true);
  }

}
