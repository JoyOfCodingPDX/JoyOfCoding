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
 * This panel is used to display and edit a <code>Student</code>'s
 * <code>Grade</code>s.
 */
public class GradePanel extends JPanel {

  private Grade grade;       // The Grade being edited/displayed
  private Student student;
  private GradeBook book;

  // GUI components we care about
  private JLabel studentNameLabel;
  private JList assignmentsList;
  private JLabel gradeLabel;
  private JTextField gradeField;
  private NotesPanel notes;
  private JList lateList;
  private JList resubmitList;

  /**
   * Creates and populates a new <code>GradePanel</code>
   */
  public GradePanel() {
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    JPanel studentNamePanel = new JPanel();
    studentNamePanel.setLayout(new BoxLayout(studentNamePanel,
                                             BoxLayout.X_AXIS));
    studentNamePanel.add(Box.createHorizontalGlue());
    this.studentNameLabel = new JLabel("Student");
    studentNamePanel.add(this.studentNameLabel);
    studentNamePanel.add(Box.createHorizontalGlue());
    this.add(studentNamePanel, BorderLayout.NORTH);

    JSplitPane centerPanel = new JSplitPane();

    JPanel listPanel = new JPanel();
    Border assignmentBorder =
      BorderFactory.createTitledBorder("Assignments");
    listPanel.setBorder(assignmentBorder);
    listPanel.setLayout(new BorderLayout());

    this.assignmentsList = new JList();
    assignmentsList.addListSelectionListener(new
      ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          // Display the grade for the currently selected
          Assignment assign = getSelectedAssignment();
          if(assign != null) {
            displayGradeFor(assign);
          }
        }
      });
    listPanel.add(new JScrollPane(assignmentsList),
                  BorderLayout.CENTER);
    centerPanel.setLeftComponent(listPanel);

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    JPanel gradePanel = new JPanel();
    gradePanel.setLayout(new BorderLayout());
    gradePanel.setBorder(BorderFactory.createTitledBorder("Grade"));
    
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    
    JPanel labels = new JPanel();
    labels.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    labels.setLayout(new GridLayout(0, 1));
    this.gradeLabel = new JLabel("Grade (out of ):");
    labels.add(this.gradeLabel);

    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(0, 1));
    this.gradeField = new JTextField(5);
    fields.add(this.gradeField);

    p.add(labels, BorderLayout.WEST);
    p.add(fields, BorderLayout.CENTER);

    gradePanel.add(p, BorderLayout.NORTH);

    this.notes = new NotesPanel();
    gradePanel.add(this.notes, BorderLayout.CENTER);

    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Assignment assign = getSelectedAssignment();
          if(assign != null && student != null) {
            Grade grade = student.getGrade(assign.getName());
            if(grade == null) {
              grade = createGrade();

            } else {
              updateGrade(grade);
            }
          }
        }
      });
    buttons.add(updateButton);
    gradePanel.add(buttons, BorderLayout.SOUTH);
    mainPanel.add(gradePanel, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
    
    JPanel latePanel = new JPanel();
    latePanel.setBorder(BorderFactory.createTitledBorder("Late"));
    latePanel.setLayout(new BorderLayout());
    this.lateList = new JList();
    latePanel.add(new JScrollPane(this.lateList), BorderLayout.CENTER);
    JPanel lateButtons = new JPanel();
    lateButtons.setLayout(new FlowLayout());
    JButton addLate = new JButton("Add");
    addLate.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Assignment assign = getSelectedAssignment();
          if(assign != null && student != null) {
            java.util.List late = student.getLate();
//             if(!late.contains(assign.getName())) {
              student.addLate(assign.getName());
              Vector v = new Vector(student.getLate());
              lateList.setListData(v);
//             }
          }
        }
      });
    lateButtons.add(addLate);
    latePanel.add(lateButtons, BorderLayout.SOUTH);

    JPanel resubmitPanel = new JPanel();
    resubmitPanel.setBorder(BorderFactory.createTitledBorder("Resubmitted"));
    resubmitPanel.setLayout(new BorderLayout());
    this.resubmitList = new JList();
    resubmitPanel.add(new JScrollPane(this.resubmitList), BorderLayout.CENTER);
    JPanel resubmitButtons = new JPanel();
    resubmitButtons.setLayout(new FlowLayout());
    JButton addResubmit = new JButton("Add");
    addResubmit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Assignment assign = getSelectedAssignment();
          if(assign != null && student != null) {
            java.util.List resubmit = student.getResubmitted();
//             if(!resubmit.contains(assign.getName())) {
              student.addResubmitted(assign.getName());
              Vector v = new Vector(student.getResubmitted());
              resubmitList.setListData(v);
            }
//           }
        }
      });
    resubmitButtons.add(addResubmit);
    resubmitPanel.add(resubmitButtons, BorderLayout.SOUTH);

    bottomPanel.add(latePanel);
    bottomPanel.add(resubmitPanel);
    
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    centerPanel.setRightComponent(mainPanel);

    this.add(centerPanel, BorderLayout.CENTER);
  }

  /**
   * Displays the assignments for a given <code>GradeBook</code>
   */
  public void displayAssignmentsFor(GradeBook book) {
    this.book = book;

    // Clear grade fields
    Vector names = new Vector(book.getAssignmentNames());
    this.assignmentsList.setListData(names);

    this.gradeLabel.setText("Grade (out of ):");
    this.gradeField.setText("");
  }

  /**
   * Displays the grades for a given <code>Student</code>
   */
  public void displayStudent(Student student) {
    // Clear any grade info
    this.gradeLabel.setText("Grade (out of ):");
    this.gradeField.setText("");
    this.notes.clearNotes();

    this.student = student;
    String name = student.getFullName();
    if(name.equals("")) {
      name = student.getId();
    }
    this.studentNameLabel.setText(name);
    this.lateList.setListData(new Vector(student.getLate()));
    this.resubmitList.setListData(new Vector(student.getResubmitted()));

    // Might as well refresh assignments list
    this.displayAssignmentsFor(this.book);
  }

  /**
   * Displays the current students grade for a given assignment
   */
  private void displayGradeFor(Assignment assign) {
    this.gradeLabel.setText("Grade (out of " + assign.getPoints() +
                            "):");
    
    if(this.student != null) {
      this.grade = student.getGrade(assign.getName());
      if(this.grade != null) {
        this.gradeField.setText("" + grade.getScore());
        this.notes.setNotable(this.grade);
        return;
      }
    }

    this.gradeField.setText("");
    this.notes.clearNotes();
  }

  /**
   * Returns the <code>Assignment</code> that is currently selected in
   * the assignmentsList.
   */
  private Assignment getSelectedAssignment() {
    String name = (String) this.assignmentsList.getSelectedValue();
    if(name != null) {
      Assignment assign = this.book.getAssignment(name);
      return(assign);

    } else {
      return(null);
    }
  }

  /**
   * Creates a new <code>Grade</code> based on the contents of this
   * <code>GradePanel</code>
   */
  private Grade createGrade() {
    Assignment assign = getSelectedAssignment();

    if(this.student == null) {
      return(null);

    } else if(assign == null) {
      String s = "Please select an assignment";
      JOptionPane.showMessageDialog(this, 
                                    new String[] {s},
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return(null);
    }

    String score = this.gradeField.getText();

    try {
      double d = Double.parseDouble(score);
      Grade grade = new Grade(assign.getName(), d);
      this.notes.addAllNotesTo(grade);
      this.student.setGrade(assign.getName(), grade);
      return(grade);

    } catch(NumberFormatException ex) {
      String s = score + " is not a number";
      JOptionPane.showMessageDialog(this, 
                                    new String[] {s},
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return(null);
    }    
  }

  /**
   * Updates the current <code>Grade</code> based on the contents of
   * this <code>GradePanel</code>
   */
  private void updateGrade(Grade grade) {
    String score = this.gradeField.getText();
    try {
      // All we can change is the score
      double d = Double.parseDouble(score);
      grade.setScore(d);

    } catch(NumberFormatException ex) {
      String s = score + " is not a number";
      JOptionPane.showMessageDialog(this, 
                                    new String[] {s},
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
      return;
    }    

    // Remember that the NotesPanel automatically updates the grade
  }

  /**
   * Test program
   */
  public static void main(String[] args) {
    final String fileName = args[0];
    String studentName = args[1];

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

    JFrame frame = new JFrame("GradePanel test");
    GradePanel gradePanel = new GradePanel();
    gradePanel.displayAssignmentsFor(book);

    Student student = book.getStudent(studentName);
    gradePanel.displayStudent(student);

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

    frame.getContentPane().add(gradePanel);
    
    frame.pack();
    frame.setVisible(true);
  }
}
