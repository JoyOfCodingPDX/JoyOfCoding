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
 * This panel displays a <code>Student</code>
 */
public class StudentPanel extends JPanel {

  private Student student;
  private NotesPanel notes;

  // GUI components we care about
  private JTextField idField;
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JTextField nickNameField;
  private JTextField emailField;
  private JTextField ssnField;
  private JTextField majorField;

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
    this.ssnField = new JTextField(11);
    fields.add(this.ssnField);
    this.majorField = new JTextField(15);
    fields.add(this.majorField);

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
          if(StudentPanel.this.student != null) {
            updateStudent(StudentPanel.this.student);
          }
        }
      });
    buttons.add(update);

    this.add(buttons, BorderLayout.SOUTH);
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
    this.ssnField.setText("");
    this.majorField.setText("");
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
    if(firstName != null && !firstName.equals("")) {
      this.firstNameField.setText(firstName);
    }

    String lastName = student.getLastName();
    if(lastName != null && !lastName.equals("")) {
      this.lastNameField.setText(lastName);
    }

    String nickName = student.getNickName();
    if(nickName != null && !nickName.equals("")) {
      this.nickNameField.setText(nickName);
    }

    String email = student.getEmail();
    if(email != null && !email.equals("")) {
      this.emailField.setText(email);
    }

    String ssn = student.getSsn();
    if(ssn != null && !ssn.equals("")) {
      this.ssnField.setText(ssn);
    }

    String major = student.getMajor();
    if(major != null && !major.equals("")) {
      this.majorField.setText(major);
    }

    this.notes.setNotable(student);
  }

  /**
   * Updates a <code>Student</code> based on the contents of this
   * <code>StudentPanel</code>.
   */
  private void updateStudent(Student student) {
    String firstName = this.firstNameField.getText();
    if(firstName != null && !firstName.equals("")) {
      student.setFirstName(firstName);
    }

    String lastName = this.lastNameField.getText();
    if(lastName != null && !lastName.equals("")) {
      student.setLastName(lastName);
    }

    String nickName = this.nickNameField.getText();
    if(nickName != null && !nickName.equals("")) {
      student.setNickName(nickName);
    }

    String email = this.emailField.getText();
    if(email != null && !email.equals("")) {
      student.setEmail(email);
    }

    String ssn = this.ssnField.getText();
    if(ssn != null && !ssn.equals("")) {
      student.setSsn(ssn);
    }

    String major = this.majorField.getText();
    if(major != null && !major.equals("")) {
      student.setMajor(major);
    }
    
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

    Student student = book.getStudent(studentId);

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

          } catch(IOException ex) {
            System.err.println("** Error while writing XML file: " + ex);
          }

          System.exit(1);
        }
      });

    frame.getContentPane().add(studentPanel);
    
    frame.pack();
    frame.setVisible(true);
  }

}
