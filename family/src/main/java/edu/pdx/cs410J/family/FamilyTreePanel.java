package edu.pdx.cs410J.family;

import java.awt.*;
import java.awt.event.*;

import java.net.*;

import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This class is a <code>JPanel</code> that can be used to display a
 * family tree. 
 */
@SuppressWarnings("serial")
public class FamilyTreePanel extends JPanel {
  protected FamilyTree tree = new FamilyTree();

  // GUI components worth holding onto
  protected JLabel sourceLocation;
  protected FamilyTreeList treeList;
  protected PersonPanel personPanel;

  /**
   * Creates a new <code>FamilyTreePanel</code>
   */
  public FamilyTreePanel() {
    this.addComponents();
  }

  /**
   * Adds the GUI components to a <code>FamilyTreePanel</code>
   */
  void addComponents() {
    System.out.println("Creating Panel");

    // Set up the content
    Dimension minSizeLeft = new Dimension(50, 100);
    this.treeList = new FamilyTreeList();
    this.treeList.setToolTipText("Click to select a person in the " +
                                 "family tree");
    this.treeList.addListSelectionListener(new ListSelectionListener()
      {
        public void valueChanged(ListSelectionEvent e) {
          Person person =
            FamilyTreePanel.this.treeList.getSelectedPerson();
          showPerson(person);
        }
      });
    this.treeList.setMinimumSize(minSizeLeft);
    JScrollPane scrollPane = new JScrollPane(this.treeList);
    scrollPane.setMinimumSize(minSizeLeft);
    
    JPanel newPersonPanel = null;
    if (this.canEdit()) {
      newPersonPanel = new JPanel();
      newPersonPanel.setLayout(new BoxLayout(newPersonPanel,
                                             BoxLayout.X_AXIS));
      newPersonPanel.add(Box.createHorizontalGlue());
      JButton newPersonButton = new JButton("New Person");
      newPersonButton.setToolTipText("Creates a new person");
      newPersonButton.setMnemonic(KeyEvent.VK_N);
      newPersonButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            newPerson();
          }
        });
      newPersonPanel.add(newPersonButton);
      newPersonPanel.add(Box.createHorizontalGlue());
    }

    JPanel treePanel = new JPanel();
    treePanel.setLayout(new BorderLayout());
    treePanel.add(scrollPane, BorderLayout.CENTER);
    if (this.canEdit()) {
      treePanel.add(newPersonPanel, BorderLayout.SOUTH);
    }

    this.personPanel = new PersonPanel(this);

    JSplitPane splitPane = 
      new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, this.personPanel);

    this.setLayout(new BorderLayout());
    this.add(splitPane, BorderLayout.CENTER);

    JPanel sourcePanel = new JPanel();
    sourcePanel.setLayout(new BoxLayout(sourcePanel, BoxLayout.X_AXIS));
    this.sourceLocation = new JLabel();
    this.sourceLocation.setToolTipText("Location of XML file");
    sourcePanel.add(this.sourceLocation);
    sourcePanel.add(Box.createHorizontalGlue());
    
    Border sourceBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    sourcePanel.setBorder(sourceBorder);
    
    this.add(sourcePanel, BorderLayout.SOUTH);
  }

  /**
   * Sets the text displayed in the source location label.
   */
  void setSourceText(String text) {
    this.sourceLocation.setText(text);
  }

  /**
   * Returns <code>true</code> if this GUI can be used to edit a person.
   */
  boolean canEdit() {
    return false;
  }

  /**
   * Sets the <code>Person</code> displayed in the GUI
   */
  void showPerson(Person person) {
    this.personPanel.showPerson(person);
  }

  /**
   * Displays the current <code>Person</code>'s mother
   */
  void displayMother() {
    Person person = this.treeList.getSelectedPerson();
    if (person != null) {
      this.treeList.setSelectedPerson(person.getMother());
    }
  }

  /**
   * Displays the current <code>Person</code>'s father
   */
  void displayFather() {
    Person person = this.treeList.getSelectedPerson();
    if (person != null) {
      this.treeList.setSelectedPerson(person.getFather());
    }
  }

  /**
   * Returns the <code>FamilyTree</code> being edited.
   */
  FamilyTree getFamilyTree() {
    return this.tree;
  }

  /**
   * Called when the family tree changes dirtiness.  Has no effect
   * with the panel.
   */
  void setDirty(boolean isDirty) {

  }

  /**
   * Called when a new person is to be created.  Has no effect with
   * the panel.
   */
  void newPerson() {

  }

  /**
   * Called when a person is to be edited.  No effect with the panel.
   */
  void editPerson() {

  }

  /**
   * Called when a marriage is added.  No effect with the panel.
   */
  void addMarriage() {

  }

  /**
   * Returns the <code>JFrame</code> associated with this GUI.
   * Returns <code>null</code> for the panel.
   */
  JFrame getFrame() {
    return null;
  }

  /**
   * Sets the source of the XML file displayed in this GUI
   */
  protected void setURLSource(URL url) {
    try {
      this.tree = parseSource(url);

    } catch (IOException ex) {
      System.out.println("source = " + this.sourceLocation + ": " + ex);
      this.sourceLocation.setText(ex.toString());
      return;

    } catch (FamilyTreeException ex) {
      this.sourceLocation.setText(ex.toString());
      return;
    }

    this.treeList.fillInList(this.tree);
    this.sourceLocation.setText(url.toExternalForm());
  }

  /**
   * Parses a <code>URL</code> and tries to extract a family tree in
   * XML format from it.
   */ 
  protected FamilyTree parseSource(URL url) 
    throws IOException, FamilyTreeException { 
    
    InputStream stream = url.openStream();

    XmlParser parser = new XmlParser(new InputStreamReader(stream));
    return parser.parse();
  }

  /**
   * Creates a <code>FamilyTreePanel</code> that is displayed inside
   * a <code>JFrame</code>
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("** No URL Specified!!");
      System.exit(1);
    }

    URL url = null;
    try {
      url = new URL(args[0]);

    } catch (MalformedURLException ex) {
      System.err.println(ex.toString());
      System.exit(1);
    }

    FamilyTreePanel viewer = new FamilyTreePanel();
    viewer.setURLSource(url);
    

    JFrame frame = new JFrame("Family Tree Panel");
    frame.getContentPane().add(viewer);

    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(0);
        }
      });

    frame.pack();
    frame.setVisible(true);
  }

}
