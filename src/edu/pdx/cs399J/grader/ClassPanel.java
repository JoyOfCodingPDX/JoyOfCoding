package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This panel displays and edits information about a class stored in a
 * <code>GradeBook</code>.
 */
public class ClassPanel extends JPanel {
  private GradeBook book;

  private JFrame frame;

  // GUI components we care about
  private JLabel classNameLabel;
  private JList assignmentsList;
  private AssignmentPanel assignmentPanel;

  private JButton newAssignmentButton;
  private JButton newStudentButton;

  /**
   * Creats a <code>ClassPanel</code> and initializes its components.
   */
  public ClassPanel(JFrame frame) {
    this.frame = frame;
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    JPanel classNamePanel = new JPanel();
    classNamePanel.setLayout(new BoxLayout(classNamePanel,
                                           BoxLayout.X_AXIS));
    classNamePanel.add(Box.createHorizontalGlue());
    this.classNameLabel = new JLabel();
    classNamePanel.add(this.classNameLabel);
    classNamePanel.add(Box.createHorizontalGlue());
    this.add(classNamePanel, BorderLayout.NORTH);
    
    // Set up the assignments panel
    JPanel assignmentsPanel = new JPanel();
    Border assignmentBorder =
      BorderFactory.createTitledBorder("Assignments");
    assignmentsPanel.setBorder(assignmentBorder);
    assignmentsPanel.setLayout(new BorderLayout());

    JPanel listPanel = new JPanel();
    listPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    listPanel.setLayout(new BorderLayout());

    this.assignmentsList = new JList();
    assignmentsList.addListSelectionListener(new
      ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          String name = (String) assignmentsList.getSelectedValue();
          if(name != null) {
            Assignment assign = book.getAssignment(name);
            if(assign == null) {
              String s = "No assignment named: " + name;
              throw new IllegalArgumentException(s);
            }
            displayAssignment(assign);
          }
        }
      });
    listPanel.add(new JScrollPane(this.assignmentsList),
                  BorderLayout.CENTER);

    this.newAssignmentButton = new JButton("Add Assignment");
    this.newAssignmentButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          createAssignment();
        }
      });
    listPanel.add(this.newAssignmentButton, BorderLayout.SOUTH);

    assignmentsPanel.add(listPanel, BorderLayout.WEST);

    JPanel infoPanel = new JPanel();
    infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    infoPanel.setLayout(new BorderLayout());
    this.assignmentPanel = new AssignmentPanel(false);
    infoPanel.add(assignmentPanel, BorderLayout.CENTER);

    JPanel p = new JPanel();
    p.setLayout(new FlowLayout());
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Get the selected assignment and update it
          String name = (String) assignmentsList.getSelectedValue();
          Assignment assign = book.getAssignment(name);

          // Update currently selected assignment
          updateAssignment(assign);
        }
      });
    p.add(updateButton);
    infoPanel.add(p, BorderLayout.SOUTH);

    assignmentsPanel.add(infoPanel, BorderLayout.EAST);

    this.add(assignmentsPanel, BorderLayout.CENTER);

    // Add a panel for adding a new student
    JPanel newStudentPanel = new JPanel();
    newStudentPanel.setLayout(new FlowLayout());
    newStudentPanel.add(new JLabel("New Student with id:"));
    final JTextField newStudentField = new JTextField(12);
    newStudentPanel.add(newStudentField);
    this.newStudentButton = new JButton("Add");
    this.newStudentButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String id = newStudentField.getText();
          if(id == null || id.equals("")) {
            String s = "No student id specified";
            JOptionPane.showMessageDialog(ClassPanel.this, 
                                          new String[] {s},
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
          }

          Student student = new Student(id);
          ClassPanel.this.book.addStudent(student);
        }
      });
    newStudentPanel.add(this.newStudentButton);
    this.add(newStudentPanel, BorderLayout.SOUTH);
  }

  /**
   * Creates a new assignment based on the contents of the "name" and
   * "max points" fields.  While we're at it, we add it to the
   * assignment book and display it in the assignment list.
   */
  private Assignment createAssignment() {
    NewAssignmentDialog dialog = new NewAssignmentDialog(this.frame);
    dialog.pack();
    dialog.setLocationRelativeTo(frame);
    dialog.setVisible(true);

    Assignment newAssign = dialog.getAssignment();

    if(newAssign != null) {
      book.addAssignment(newAssign);
      this.displayAssignments(book);
      this.assignmentsList.setSelectedValue(newAssign.getName(),
                                            true);
    }
    return(newAssign);
  }

  /**
   * Fills in the contents of an <code>Assignment</code> based on the
   * contents of the GUI.
   */
  private void updateAssignment(Assignment assign) {
    this.assignmentPanel.updateAssignment(assign);
  }

  /**
   * Sets the <code>GradeBook</code> that is displayed and edited in
   * this <code>ClassPanel</code>.
   */
  void displayGradeBook(GradeBook book) {
    this.book = book;
    this.newAssignmentButton.setEnabled(true);
    this.newStudentButton.setEnabled(true);

    // Display name of the class
    this.classNameLabel.setText(book.getClassName());

    this.displayAssignments(book);
    this.assignmentsList.clearSelection();  // No initial selection
  }

  /**
   * Displays the names of all of the assignments in a given grade
   * book in the assignmentsList.
   */
  void displayAssignments(GradeBook book) {
    // Display all of the assignments
    Vector assignmentNames = new Vector();
    assignmentNames.addAll(book.getAssignmentNames());
    this.assignmentsList.setListData(assignmentNames);
  }
  
  /**
   * Displays an assignment in the appropriate fields
   */
  private void displayAssignment(Assignment assign) {
    this.assignmentPanel.displayAssignment(assign);
  }

  /**
   * Test program that displays and edits the information in a given
   * grade book stored in an XML file.
   */
  public static void main(String[] args) {
    if(args.length < 1) {
      System.err.println("** usage: java ClassPanel xmlFile");
      System.exit(1);
    }

    final String fileName = args[0];

    GradeBook book = null;
    try {
      XmlParser parser = new XmlParser(fileName);
      book = parser.parse();

    } catch(FileNotFoundException ex) {
      System.err.println("** Could not find file: " + ex.getMessage());
      System.exit(1);
      
    } catch(IOException ex) {
      System.err.println("** IOException during parsing: " + ex.getMessage());
      System.exit(1);

    } catch(ParserException ex) {
      System.err.println("** Error during parsing: " + ex);
      System.exit(1);
    }

    JFrame frame = new JFrame("ClassPanel test");
    ClassPanel classPanel = new ClassPanel(frame);
    classPanel.displayGradeBook(book);

    final GradeBook theBook = book;
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          // Write changes to grade book back to file
          try {
            XmlDumper dumper = new XmlDumper(fileName);
            dumper.dump(theBook);

          } catch(IOException ex) {
            System.err.println("** Error while writing XML file: " + ex);
          }

          System.exit(1);
        }
      });

    frame.getContentPane().add(classPanel);
    
    frame.pack();
    frame.setVisible(true);
  }

}
