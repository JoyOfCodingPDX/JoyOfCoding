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
  private static String QUIZ = "Quiz";
  private static String PROJECT = "Project";
  private static String OTHER = "Other";

  private GradeBook book;
  private Vector assignmentNames = new Vector();

  // GUI components we care about
  private JLabel classNameLabel;
  private JList assignmentsList;
  private JTextField nameField;
  private JComboBox typeBox;
  private JTextField pointsField;
  private JTextField descriptionField;

  private JButton newAssignmentButton;
  private JButton newStudentButton;

  /**
   * Creats a <code>ClassPanel</code> and initializes its components.
   */
  public ClassPanel() {
    this.setLayout(new BorderLayout());

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
    listPanel.setLayout(new BorderLayout());
    this.assignmentsList = new JList();
    listPanel.add(new JScrollPane(this.assignmentsList),
                  BorderLayout.CENTER);
    this.newAssignmentButton = new JButton("Add Assignment");
    this.newAssignmentButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Get the name and max points of the assignment
          String name = nameField.getText();
          if(name == null || name.equals("")) {
            String s = "No assignment name specified";
            JOptionPane.showMessageDialog(ClassPanel.this, 
                                          new String[] {s},
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
          }

          String points = pointsField.getText();
          if(points == null || points.equals("")) {
            String s = "No points value specified";
            JOptionPane.showMessageDialog(ClassPanel.this, 
                                          new String[] {s},
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
          }

          // Create a new Assignment object
          try {
            double d = Double.parseDouble(points);
            Assignment newAssign = new Assignment(name, d);
            fillInAssignment(newAssign);
            book.addAssignment(newAssign);

          } catch(NumberFormatException ex) {
            String s = points + " is not a number";
            JOptionPane.showMessageDialog(ClassPanel.this, 
                                          new String[] {s},
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
          }
        }
      });
    listPanel.add(this.newAssignmentButton, BorderLayout.SOUTH);

    assignmentsPanel.add(listPanel, BorderLayout.WEST);

    // Set up the panel containing info about an assignment
    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

    p.add(new JLabel("Name:"));
    this.nameField = new JTextField(8);
    p.add(this.nameField);
    p.add(Box.createHorizontalGlue());
    infoPanel.add(p);

    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(new JLabel("Max points:"));
    this.pointsField = new JTextField(5);
    p.add(this.pointsField);
    p.add(Box.createHorizontalGlue());
    infoPanel.add(p);

    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(new JLabel("Type:"));
    this.typeBox = new JComboBox();
    this.typeBox.addItem(QUIZ);
    this.typeBox.addItem(PROJECT);
    this.typeBox.addItem(OTHER);
    p.add(this.typeBox);
    p.add(Box.createHorizontalGlue());
    infoPanel.add(p);

    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(new JLabel("Description:"));
    this.descriptionField = new JTextField(20);
    p.add(this.descriptionField);
    p.add(Box.createHorizontalGlue());
    infoPanel.add(p);

    infoPanel.add(Box.createVerticalGlue());

    p = new JPanel();
    p.setLayout(new FlowLayout());
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
        }
      });
    p.add(updateButton);
    infoPanel.add(p);

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
   * Fills in the contents of an <code>Assignment</code> based on the
   * contents of the GUI.
   */
  private void fillInAssignment(Assignment assign) {
    
  }

  /**
   * Sets the <code>GradeBook</code> that is displayed and edited in
   * this <code>ClassPanel</code>.
   */
  void setGradeBook(GradeBook book) {
    this.book = book;
    this.newAssignmentButton.setEnabled(true);
    this.newStudentButton.setEnabled(true);

    // Display name of the class
    this.classNameLabel.setText(book.getClassName());

    // Display all of the assignments
    this.assignmentNames = new Vector();
    this.assignmentNames.addAll(book.getAssignmentNames());
    this.assignmentsList.setListData(this.assignmentNames);
    this.assignmentsList.clearSelection();
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

    String fileName = args[0];

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

    ClassPanel classPanel = new ClassPanel();
    classPanel.setGradeBook(book);

    JFrame frame = new JFrame("StudentsList test");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    frame.getContentPane().add(classPanel);
    
    frame.pack();
    frame.setVisible(true);
  }

}
