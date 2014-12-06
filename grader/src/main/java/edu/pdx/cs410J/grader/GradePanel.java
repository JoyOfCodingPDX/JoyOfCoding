package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;

/**
 * This panel is used to display and edit a <code>Student</code>'s
 * <code>Grade</code>s.
 */
@SuppressWarnings("serial")
public class GradePanel extends JPanel {

  private Student student;
  private GradeBook book;

  // GUI components we care about
  private JLabel studentNameLabel;
  private JList<String> assignmentsList;
  private JLabel gradeLabel;
  private JLabel assignmentLabel;
  private JTextField gradeField;
  private NotesPanel notes;
  private JList<String> lateList;
  private JList<String> resubmitList;
  private JList<LocalDateTime> submissionTimesList;

  /** The most recently selected index in the assignments list */
  private int assignmentIndex = -1;

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Creates and populates a new <code>GradePanel</code>
   */
  public GradePanel() {
    this.setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    this.add(createStudentNamePanel(), BorderLayout.NORTH);

    JSplitPane centerPanel = new JSplitPane();

    centerPanel.setLeftComponent(createAssignmentListPanel());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    mainPanel.add(createGradeDetailPanel(), BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

    bottomPanel.add(createLatePanel());
    bottomPanel.add(createResubmitPanel());
    
    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    centerPanel.setRightComponent(mainPanel);

    this.add(centerPanel, BorderLayout.CENTER);
  }

  private JPanel createStudentNamePanel() {
    JPanel studentNamePanel = new JPanel();
    studentNamePanel.setLayout(new BoxLayout(studentNamePanel,
                                             BoxLayout.X_AXIS));
    studentNamePanel.add(Box.createHorizontalGlue());
    this.studentNameLabel = new JLabel("Student");
    studentNamePanel.add(this.studentNameLabel);
    studentNamePanel.add(Box.createHorizontalGlue());
    return studentNamePanel;
  }

  private JPanel createAssignmentListPanel() {
    JPanel listPanel = new JPanel();
    Border assignmentBorder =
      BorderFactory.createTitledBorder("Assignments");
    listPanel.setBorder(assignmentBorder);
    listPanel.setLayout(new BorderLayout());

    this.assignmentsList = new JList<>();
    assignmentsList.addListSelectionListener(e -> {
      Assignment assign = getSelectedAssignment();
      if (assign != null) {
        displayGradeFor(assign);
      }
    });
    listPanel.add(new JScrollPane(assignmentsList),
                  BorderLayout.CENTER);
    return listPanel;
  }

  private JPanel createResubmitPanel() {
    JPanel resubmitPanel = new JPanel();
    resubmitPanel.setBorder(BorderFactory.createTitledBorder("Resubmitted"));
    resubmitPanel.setLayout(new BorderLayout());
    this.resubmitList = new JList<>();
    resubmitPanel.add(new JScrollPane(this.resubmitList), BorderLayout.CENTER);
    JPanel resubmitButtons = new JPanel();
    resubmitButtons.setLayout(new FlowLayout());
    JButton addResubmit = new JButton("Add");
    addResubmit.addActionListener(e -> {
      Assignment assign = getSelectedAssignment();
      if (assign != null && student != null) {
          student.addResubmitted(assign.getName());
          Vector<String> v = new Vector<>(student.getResubmitted());
          resubmitList.setListData(v);
        }
    });
    resubmitButtons.add(addResubmit);
    resubmitPanel.add(resubmitButtons, BorderLayout.SOUTH);
    return resubmitPanel;
  }

  private JPanel createLatePanel() {
    JPanel latePanel = new JPanel();
    latePanel.setBorder(BorderFactory.createTitledBorder("Late"));
    latePanel.setLayout(new BorderLayout());
    this.lateList = new JList<>();
    latePanel.add(new JScrollPane(this.lateList), BorderLayout.CENTER);
    JPanel lateButtons = new JPanel();
    lateButtons.setLayout(new FlowLayout());
    JButton addLate = new JButton("Add");
    addLate.addActionListener(e -> {
      Assignment assign = getSelectedAssignment();
      if (assign != null && student != null) {
          student.addLate(assign.getName());
          Vector<String> v = new Vector<>(student.getLate());
          lateList.setListData(v);
      }
    });
    lateButtons.add(addLate);
    latePanel.add(lateButtons, BorderLayout.SOUTH);
    return latePanel;
  }

  private JPanel createGradeDetailPanel() {
    JPanel gradePanel = new JPanel();
    gradePanel.setLayout(new BorderLayout());
    gradePanel.setBorder(BorderFactory.createTitledBorder("Grade"));

    gradePanel.add(createGradeInfoPanel(), BorderLayout.NORTH);

    this.notes = new NotesPanel();
    gradePanel.add(this.notes, BorderLayout.CENTER);

    gradePanel.add(createSubmissionsList(), BorderLayout.EAST);

    gradePanel.add(createGradeButtonsPanel(), BorderLayout.SOUTH);

    return gradePanel;
  }

  private Component createSubmissionsList() {
    JPanel submissionsPanel = new JPanel();
    submissionsPanel.setLayout(new BorderLayout());
    submissionsPanel.setBorder(BorderFactory.createTitledBorder("Submissions"));

    this.submissionTimesList = new JList<>();
    this.submissionTimesList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object dateTime, int index, boolean isSelected, boolean cellHasFocus) {
        LocalDateTime submissionTime = (LocalDateTime) dateTime;
        String value = submissionTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });

    submissionsPanel.add(new JScrollPane(this.submissionTimesList), BorderLayout.CENTER);

    return submissionsPanel;
  }

  private JPanel createGradeButtonsPanel() {
    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    JButton updateButton = new JButton("Update");
    updateButton.addActionListener(e -> createOrUpdateGrade());
    buttons.add(updateButton);
    return buttons;
  }

  private JPanel createGradeInfoPanel() {
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());

    this.assignmentLabel = new JLabel("Assignment");
    p.add(this.assignmentLabel, BorderLayout.NORTH);

    JPanel labels = new JPanel();
    labels.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    labels.setLayout(new GridLayout(0, 1));
    this.gradeLabel = new JLabel("Grade (out of ):", JLabel.CENTER);
    labels.add(this.gradeLabel);

    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(0, 1));
    this.gradeField = new JTextField(5);
    this.gradeField.addActionListener(e -> createOrUpdateGrade());
    fields.add(this.gradeField);

    p.add(labels, BorderLayout.WEST);
    p.add(fields, BorderLayout.CENTER);
    return p;
  }

  private void createOrUpdateGrade() {
    Assignment assign = getSelectedAssignment();
    if (assign != null && student != null) {
      Grade grade = student.getGrade(assign.getName());
      if (grade == null) {
        createGrade();

      } else {
        updateGrade(grade);
      }
    }
  }

  /**
   * Displays the assignments for a given <code>GradeBook</code>
   */
  public void displayAssignmentsFor(GradeBook book) {
    this.book = book;

    // Clear grade fields
    Vector<String> names = new Vector<>(book.getAssignmentNames());
    this.assignmentsList.setListData(names);

    this.assignmentLabel.setText("Assignment");
    this.gradeLabel.setText("Grade (out of ):");
    this.gradeField.setText("");
  }

  /**
   * Displays the grades for a given <code>Student</code>
   */
  public void displayStudent(Student student) {
    // Clear any grade info
    this.assignmentLabel.setText("Assignment");
    this.gradeLabel.setText("Grade (out of ):");
    this.gradeField.setText("");
    this.notes.clearNotes();

    this.student = student;
    String name = student.getFullName();
    if (name.equals("")) {
      name = student.getId();
    }
    this.studentNameLabel.setText(name + " (" + student.getId() + ")");
    this.lateList.setListData(new Vector<>(student.getLate()));
    this.resubmitList.setListData(new Vector<>(student.getResubmitted()));

    // Might as well refresh assignments list
    this.displayAssignmentsFor(this.book);

    // Display the grade for the currently selected
    Assignment assign = getSelectedAssignment();
    if (assign != null) {
      displayGradeFor(assign);
    }
  }

  /**
   * Displays the current students grade for a given assignment
   */
  private void displayGradeFor(Assignment assign) {
    this.assignmentLabel.setText("Assignment " + assign.getName() +
                                 " (" + assign.getDescription() + ")");
    this.gradeLabel.setText("Grade (out of " + assign.getPoints() +
                            "):");
    
    if (this.student != null) {
      Grade grade = student.getGrade(assign.getName());
      if (grade != null) {
        this.gradeField.setText(String.valueOf(grade.getScore()));
        this.notes.setNotable(grade);
        this.submissionTimesList.setModel(createListModel(grade.getSubmissionTimes()));
        return;
      }
    }

    this.gradeField.setText("");
    this.notes.clearNotes();
  }

  private ListModel<LocalDateTime> createListModel(List<LocalDateTime> submissionTimes) {
    return new AbstractListModel<LocalDateTime>() {
      @Override
      public int getSize() {
        return submissionTimes.size();
      }

      @Override
      public LocalDateTime getElementAt(int index) {
        return submissionTimes.get(index);
      }
    };
  }

  /**
   * Returns the <code>Assignment</code> that is currently selected in
   * the assignmentsList.
   */
  private Assignment getSelectedAssignment() {
    String name = this.assignmentsList.getSelectedValue();
    if (name != null) {
      this.assignmentIndex = this.assignmentsList.getSelectedIndex();
      return this.book.getAssignment(name);

    } else if (this.assignmentIndex == -1) {
      return null;

    } else if (this.assignmentIndex >=
               this.assignmentsList.getModel().getSize()) {
      // Changed grade book?
      this.assignmentIndex = -1;
      return null;

    } else {
      this.assignmentsList.setSelectedIndex(this.assignmentIndex);
      return getSelectedAssignment();
    }
  }

  /**
   * Creates a new <code>Grade</code> based on the contents of this
   * <code>GradePanel</code>
   */
  private Grade createGrade() {
    Assignment assign = getSelectedAssignment();

    if (this.student == null) {
      return null;

    } else if (assign == null) {
      String s = "Please select an assignment";
      return error(s);
    }

    String score = this.gradeField.getText();

    try {
      double d = Double.parseDouble(score);
      Grade grade = new Grade(assign.getName(), d);
      this.notes.setNotable(grade);
      this.student.setGrade(assign.getName(), grade);
      return grade;

    } catch (NumberFormatException ex) {
      return error(score + " is not a number");
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

    } catch (NumberFormatException ex) {
      error(score + " is not a number");
    }

    // Remember that the NotesPanel automatically updates the grade
  }

  private <T> T error(String message) {
    JOptionPane.showMessageDialog(this,
      new String[]{message},
      "Error",
      JOptionPane.ERROR_MESSAGE);
    return null;
  }

  /**
   * Test program
   */
  public static void main(String[] args) {
    final String fileName = args[0];
    String studentName = args[1];

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

    JFrame frame = new JFrame("GradePanel test");
    GradePanel gradePanel = new GradePanel();
    gradePanel.displayAssignmentsFor(book);

    Student student =
      book.getStudent(studentName).orElseThrow(() -> new IllegalStateException("No student with id " + studentName));
    gradePanel.displayStudent(student);

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

    frame.getContentPane().add(gradePanel);
    
    frame.pack();
    frame.setVisible(true);
  }
}
