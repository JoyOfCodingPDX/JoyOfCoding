package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Supplier;

/**
 * This panel displays a <code>Student</code>
 */
@SuppressWarnings("serial")
public class StudentPanel extends JPanel {

  private Student student;
  private NotesPanel notes;

  // GUI components we care about
  private JTextField idField;
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JTextField nickNameField;
  private JTextField emailField;
  private JTextField majorField;
  private JTextField canvasId;
  private final JComboBox<LetterGrade> letterGraderComboBox;
  private final JComboBox<Student.Section> enrolledSectionComboBox;

  /**
   * Creates and lays out a new <code>StudentPanel</code>
   */
  public StudentPanel() {
    this.setLayout(new BorderLayout());

    // Center panel that contains information about the student
    JPanel infoPanel = new JPanel();
    infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    infoPanel.setLayout(new BorderLayout());

    JPanel labels = new JPanel();
    labels.setLayout(new GridLayout(0, 1));
    labels.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    labels.add(new JLabel("Id:"));
    labels.add(new JLabel("First name:"));
    labels.add(new JLabel("Last name:"));
    labels.add(new JLabel("Nick name:"));
    labels.add(new JLabel("Email:"));
    labels.add(new JLabel("SSN:"));
    labels.add(new JLabel("Major:"));
    labels.add(new JLabel("Canvas Id:"));
    labels.add(new JLabel("Letter Grade:"));
    labels.add(new JLabel("Enrolled Section:"));

    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(0, 1));
    this.idField = new JTextField(10);
    this.idField.setEditable(false);
    fields.add(this.idField);
    this.firstNameField = new JTextField(10);
    fields.add(this.firstNameField);
    this.lastNameField = new JTextField(10);
    fields.add(this.lastNameField);
    this.nickNameField = new JTextField(10);
    fields.add(this.nickNameField);
    this.emailField = new JTextField(15);
    fields.add(this.emailField);
    this.majorField = new JTextField(15);
    fields.add(this.majorField);
    this.canvasId = new JTextField(10);
    fields.add(this.canvasId);
    this.letterGraderComboBox = createLetterGradeComboBox();
    fields.add(this.letterGraderComboBox);
    this.enrolledSectionComboBox= createEnrolledSectionComboBox();
    fields.add(this.enrolledSectionComboBox);

    infoPanel.add(labels, BorderLayout.WEST);
    infoPanel.add(fields, BorderLayout.CENTER);

    this.add(infoPanel, BorderLayout.NORTH);

    // Add a NotePanel
    this.notes = new NotesPanel();
    this.add(notes, BorderLayout.CENTER);

    // Update button
    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    JButton update = new JButton("Update");
    update.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (StudentPanel.this.student != null) {
            updateStudent(StudentPanel.this.student);
          }
        }
      });
    buttons.add(update);

    this.add(buttons, BorderLayout.SOUTH);
  }

  private JComboBox<Student.Section> createEnrolledSectionComboBox() {
    return createComboBoxWithEnumValues(Student.Section.values());
  }

  private JComboBox<LetterGrade> createLetterGradeComboBox() {
    return createComboBoxWithEnumValues(LetterGrade.values());
  }

  private <E> JComboBox<E> createComboBoxWithEnumValues(E[] values) {
    Vector<E> vector = new Vector<>();
    vector.add(null);
    vector.addAll(Arrays.asList(values));
    return new JComboBox<>(vector);
  }

  /**
   * Clears the contents of the student fields
   */
  void clearContents() {
    this.idField.setText("");
    this.firstNameField.setText("");
    this.lastNameField.setText("");
    this.nickNameField.setText("");
    this.emailField.setText("");
    this.majorField.setText("");
    this.canvasId.setText("");
    this.letterGraderComboBox.setSelectedItem(null);
    this.enrolledSectionComboBox.setSelectedItem(null);
    this.notes.clearNotes();
  }

  /**
   * Displays a <code>Student</code> in this
   * <code>StudentPanel</code>.
   */
  public void displayStudent(Student student) {
    this.student = student;
    this.clearContents();

//     System.out.println("Displaying student: " +
//                        student.getDescription());

    // Fill in GUI components
    this.idField.setText(student.getId());

    String firstName = student.getFirstName();
    if (firstName != null && !firstName.equals("")) {
      this.firstNameField.setText(firstName);
    }

    String lastName = student.getLastName();
    if (lastName != null && !lastName.equals("")) {
      this.lastNameField.setText(lastName);
    }

    String nickName = student.getNickName();
    if (nickName != null && !nickName.equals("")) {
      this.nickNameField.setText(nickName);
    }

    String email = student.getEmail();
    if (email != null && !email.equals("")) {
      this.emailField.setText(email);
    }

    String major = student.getMajor();
    if (major != null && !major.equals("")) {
      this.majorField.setText(major);
    }

    String canvasId = student.getCanvasId();
    if (canvasId != null && !canvasId.equals("")) {
      this.canvasId.setText(canvasId);
    }

    LetterGrade letterGrade = student.getLetterGrade();
    if (letterGrade != null) {
      this.letterGraderComboBox.setSelectedItem(letterGrade);
    }

    Student.Section section = student.getEnrolledSection();
    if (section != null) {
      this.enrolledSectionComboBox.setSelectedItem(section);
    }

    this.notes.setNotable(student);
  }

  /**
   * Updates a <code>Student</code> based on the contents of this
   * <code>StudentPanel</code>.
   */
  private void updateStudent(Student student) {
    String firstName = this.firstNameField.getText();
    if (firstName != null) {
      student.setFirstName(firstName);
    }

    String lastName = this.lastNameField.getText();
    if (lastName != null) {
      if (lastName.equals("")) {
        student.setLastName(null);
      } else {
        student.setLastName(lastName);
      }
    }

    String nickName = this.nickNameField.getText();
    if (nickName != null) {
      if (nickName.equals("")) {
        student.setNickName(null);
      } else {
        student.setNickName(nickName);
      }
    }

    String email = this.emailField.getText();
    if (email != null) {
      if (email.equals("")) {
        student.setEmail(null);
      } else {
        student.setEmail(email);
      }
    }

    String major = this.majorField.getText();
    if (major != null) {
      if (major.equals("")) {
        student.setMajor(null);
      } else {
        student.setMajor(major);
      }
    }
    
    String canvasId = this.canvasId.getText();
    if (canvasId != null) {
      if (canvasId.equals("")) {
        student.setCanvasId(null);
      } else {
        student.setCanvasId(canvasId);
      }
    }

    LetterGrade letterGrade = (LetterGrade) this.letterGraderComboBox.getSelectedItem();
    student.setLetterGrade(letterGrade);

    Student.Section section = (Student.Section) this.enrolledSectionComboBox.getSelectedItem();
    student.setEnrolledSection(section);

    // The NotesPanel takes care of adding notes

//     System.out.println("Updated student: " +
//                        student.getDescription());
  }

  /**
   * Test program
   */
  public static void main(String[] args) {
    final String fileName = args[0];
    String studentId = args[1];

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

    Student student = book.getStudent(studentId).orElseThrow(cannotFindStudent(studentId));

    StudentPanel studentPanel = new StudentPanel();
    studentPanel.displayStudent(student);

    JFrame frame = new JFrame("StudentPanel test");
    final GradeBook theBook = book;
    frame.addWindowListener(new WindowAdapter() {
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

    frame.getContentPane().add(studentPanel);
    
    frame.pack();
    frame.setVisible(true);
  }

  @SuppressWarnings("ThrowableInstanceNeverThrown")
  private static Supplier<IllegalStateException> cannotFindStudent(String studentId) {
    return () -> new IllegalStateException("Cannot find student with id " + studentId);
  }

}
