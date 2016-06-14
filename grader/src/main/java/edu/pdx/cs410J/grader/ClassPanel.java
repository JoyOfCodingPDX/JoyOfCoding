package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

/**
 * This panel displays and edits information about a class stored in a
 * <code>GradeBook</code>.
 */
@SuppressWarnings("serial")
public class ClassPanel extends JPanel {
  private GradeBook book;

  private JFrame frame;

  // GUI components we care about
  private JLabel classNameLabel;
  private JList<String> assignmentsList;
  private AssignmentPanel assignmentPanel;

  private JButton newAssignmentButton;
  private JButton newStudentButton;
  private LetterGradeRangesPanel letterGradeRangesPanel;

  /**
   * Creats a <code>ClassPanel</code> and initializes its components.
   */
  public ClassPanel(JFrame frame) {
    this.frame = frame;
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    addClassNamePanel();
    addAssignmentsPanel();
    addLetterGradeRangesPanel();
    addNewStudentPanel();
  }

  private void addLetterGradeRangesPanel() {
    JPanel letterGradeRangesPanel = new JPanel();
    Border letterGradeRangesBorder = BorderFactory.createTitledBorder("Letter Grades");
    letterGradeRangesPanel.setBorder(letterGradeRangesBorder);
    letterGradeRangesPanel.setLayout(new BorderLayout());

    this.letterGradeRangesPanel = new LetterGradeRangesPanel();
    letterGradeRangesPanel.add(this.letterGradeRangesPanel);
    this.add(letterGradeRangesPanel, BorderLayout.EAST);
  }

  private void addAssignmentsPanel() {
    // Set up the assignments panel
    JPanel assignmentsPanel = new JPanel();
    Border assignmentBorder =
      BorderFactory.createTitledBorder("Assignments");
    assignmentsPanel.setBorder(assignmentBorder);
    assignmentsPanel.setLayout(new BorderLayout());

    JPanel listPanel = addAssignmentsListPanel();
    addAddAssignmentButton(listPanel);
    assignmentsPanel.add(listPanel, BorderLayout.WEST);
    addInfoPanel(assignmentsPanel);
    this.add(assignmentsPanel, BorderLayout.CENTER);
  }

  private JPanel addAssignmentsListPanel() {
    JPanel listPanel = new JPanel();
    listPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    listPanel.setLayout(new BorderLayout());

    this.assignmentsList = new JList<>();
    assignmentsList.addListSelectionListener(new
      ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          String name = assignmentsList.getSelectedValue();
          if (name != null) {
            Assignment assign = book.getAssignment(name);
            if (assign == null) {
              String s = "No assignment named: " + name;
              throw new IllegalArgumentException(s);
            }
            displayAssignment(assign);
          }
        }
      });
    listPanel.add(new JScrollPane(this.assignmentsList),
                  BorderLayout.CENTER);
    return listPanel;
  }

  private void addNewStudentPanel() {
    // Add a panel for adding a new student
    JPanel newStudentPanel = new JPanel();
    newStudentPanel.setLayout(new FlowLayout());
    newStudentPanel.add(new JLabel("New Student with id:"));
    final JTextField newStudentField = new JTextField(12);
    newStudentPanel.add(newStudentField);
    this.newStudentButton = new JButton("Add");
    this.newStudentButton.setEnabled(false);
    this.newStudentButton.addActionListener(e -> {
      String id = newStudentField.getText();
      if (id == null || id.equals("")) {
        showErrorMessageDialog("No student id specified");
        return;
      }

      if (ClassPanel.this.book == null) {
        showErrorMessageDialog("No grade book opened");
        return;
      }

      Student student = new Student(id);
      ClassPanel.this.book.addStudent(student);
      newStudentField.setText("");
    });
    newStudentPanel.add(this.newStudentButton);
    this.add(newStudentPanel, BorderLayout.SOUTH);
  }

  private void addAddAssignmentButton(JPanel listPanel) {
    this.newAssignmentButton = new JButton("Add Assignment");
    this.newAssignmentButton.setEnabled(false);
    this.newAssignmentButton.addActionListener(e -> {
      createAssignment();
    });
    listPanel.add(this.newAssignmentButton, BorderLayout.SOUTH);
  }

  private void addInfoPanel(JPanel assignmentsPanel) {
    JPanel infoPanel = new JPanel();
    infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    infoPanel.setLayout(new BorderLayout());
    this.assignmentPanel = new AssignmentPanel(false);
    infoPanel.add(assignmentPanel, BorderLayout.CENTER);

    addUpdateButton(infoPanel);

    assignmentsPanel.add(infoPanel, BorderLayout.CENTER);
  }

  private void addUpdateButton(JPanel infoPanel) {
    JPanel p = new JPanel();
    p.setLayout(new FlowLayout());
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Get the selected assignment and update it
          String name = assignmentsList.getSelectedValue();
          Assignment assign = book.getAssignment(name);

          // Update currently selected assignment
          updateAssignment(assign);
        }
      });
    p.add(updateButton);
    infoPanel.add(p, BorderLayout.SOUTH);
  }

  private void addClassNamePanel() {
    JPanel classNamePanel = new JPanel();
    classNamePanel.setLayout(new BoxLayout(classNamePanel,
                                           BoxLayout.X_AXIS));
    classNamePanel.add(Box.createHorizontalGlue());
    this.classNameLabel = new JLabel();
    classNamePanel.add(this.classNameLabel);
    classNamePanel.add(Box.createHorizontalGlue());
    this.add(classNamePanel, BorderLayout.NORTH);
  }

  private void showErrorMessageDialog(String message) {
    JOptionPane.showMessageDialog(ClassPanel.this,
      new String[]{message},
      "Error",
      JOptionPane.ERROR_MESSAGE);
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

    if (newAssign != null) {
      book.addAssignment(newAssign);
      this.displayAssignments(book);
      this.assignmentsList.setSelectedValue(newAssign.getName(),
                                            true);
    }
    return newAssign;
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
    double points = 0.0d;
    for (String name : book.getAssignmentNames()) {
      Assignment assign = book.getAssignment( name );
      points += assign.getPoints();
    }

    String className =
      String.format( "%s (%d students, %.2f points)", book.getClassName(), book.getStudentIds().size(), points );
    this.classNameLabel.setText(className);

    this.displayAssignments(book);
    this.assignmentsList.clearSelection();  // No initial selection

    displayLetterGradeRanges(book);
  }

  private void displayLetterGradeRanges(GradeBook book) {
    this.letterGradeRangesPanel.displayLetterGradeRanges(book.getLetterGradeRanges(Student.Section.UNDERGRADUATE));
  }

  /**
   * Displays the names of all of the assignments in a given grade
   * book in the assignmentsList.
   */
  void displayAssignments(GradeBook book) {
    // Display all of the assignments
    Vector<String> assignmentNames = new Vector<>();
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
    if (args.length < 1) {
      System.err.println("** usage: java ClassPanel xmlFile");
      System.exit(1);
    }

    final String fileName = args[0];

    GradeBook book = null;
    try {
      XmlGradeBookParser parser = new XmlGradeBookParser(fileName);
      book = parser.parse();

    } catch (FileNotFoundException ex) {
      System.err.println("** Could not find file: " + ex.getMessage());
      System.exit(1);
      
    } catch (IOException ex) {
      System.err.println("** IOException during parsing: " + ex.getMessage());
      System.exit(1);

    } catch (ParserException ex) {
      System.err.println("** Error during parsing: " + ex);
      System.exit(1);
    }

    JFrame frame = new JFrame("ClassPanel test");
    ClassPanel classPanel = new ClassPanel(frame);
    classPanel.displayGradeBook(book);

    final GradeBook theBook = book;
    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          // Write changes to grade book back to file
          try {
            XmlDumper dumper = new XmlDumper(fileName);
            dumper.dump(theBook);

          } catch (IOException ex) {
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
