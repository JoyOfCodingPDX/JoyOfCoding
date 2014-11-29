package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * This panel displays the contents of a grade book.  On the left-hand
 * side of the panel is a list of the students in the class.  In the
 * center of the panel is a tabbed pane that lets you view/edit
 * information about the class, a student, or a student's grades.
 */
@SuppressWarnings("serial")
public class GradeBookPanel extends JPanel {
  static final int BY_ID = 1;
  static final int BY_NAME = 2;

  private GradeBook book;  // The GradeBook being displayed
  private StudentsModel model;

  // GUI components we care about
  private JList students;
  private ClassPanel classPanel;
  private StudentPanel studentPanel;
  private GradePanel gradePanel;

  /**
   * Creates a new <code>GradeBookPanel</code> and lays out all of its
   * components.
   */
  public GradeBookPanel(JFrame parent) {
    this.classPanel = new ClassPanel(parent);
    this.studentPanel = new StudentPanel();
    this.gradePanel = new GradePanel();

    JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    tabs.addTab("Class", this.classPanel);
    tabs.addTab("Student", this.studentPanel);
    tabs.addTab("Grades", this.gradePanel);

    JSplitPane split = new JSplitPane();
    split.setRightComponent(tabs);

    JPanel leftPanel = new JPanel();
    leftPanel.setLayout(new BorderLayout());
    leftPanel.add(new JLabel("Students", JLabel.CENTER),
                  BorderLayout.NORTH);

    this.students = new JList();
    this.students.addListSelectionListener(new
      ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          if (GradeBookPanel.this.model == null) {
            return;
          }
          
          int index = students.getSelectedIndex();

          if (index >= 0) {
            String id = model.getIdAt(index);
            displayStudent(id);

          } else {
            GradeBookPanel.this.studentPanel.clearContents();
          }
        }
      });
    this.students.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));

    leftPanel.add(new JScrollPane(this.students), BorderLayout.CENTER);
    JPanel buttons = new JPanel();
    buttons.setLayout(new FlowLayout());
    ButtonGroup group = new ButtonGroup();

    JRadioButton idButton = new JRadioButton("By id");
    idButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          displayStudents(BY_ID);
        }
      });
    group.add(idButton);
    buttons.add(idButton);

    JRadioButton nameButton = new JRadioButton("By name");
    nameButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          displayStudents(BY_NAME);
        }
      });
    group.add(nameButton);
    buttons.add(nameButton);
    leftPanel.add(buttons, BorderLayout.SOUTH);

    split.setLeftComponent(leftPanel);

    this.setLayout(new BorderLayout());
    this.add(split, BorderLayout.CENTER);
  }

  /**
   * Displays information about a given student
   */
  private void displayStudent(String id) {
    Student student =
      this.book.getStudent(id).orElseThrow(() -> new IllegalStateException("No student with id " + id));
    this.studentPanel.displayStudent(student);
    this.gradePanel.displayStudent(student);
  }

  /**
   * Displays the names of the students in the class as either their
   * full names or by the ids.
   */
  private void displayStudents(int sortOrder) {
    if (this.book == null) {
      // Nothing to do
      return;
    }

    this.model = new StudentsModel(this.book, sortOrder);
    this.students.setModel(model);
  }

  /**
   * Displays the contents of a given <code>GradeBook</code>
   */
  void displayGradeBook(GradeBook book) {
    this.book = book;
    this.displayStudents(BY_ID);
    this.classPanel.displayGradeBook(book);
    this.gradePanel.displayAssignmentsFor(book);
    this.studentPanel.clearContents();
  }

  /**
   * Test program
   */
  public static void main(String[] args) {
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

    JFrame frame = new JFrame("GradeBookPanel test");
    GradeBookPanel bookPanel = new GradeBookPanel(frame);
    bookPanel.displayGradeBook(book);

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

    frame.getContentPane().add(bookPanel);
    
    frame.pack();
    frame.setVisible(true);
  }

}

/**
 * Class used for displaying either student ids or their full names in
 * a <code>JList</code> 
 */
@SuppressWarnings("serial")
class StudentsModel extends AbstractListModel {

  private ArrayList<String> ids;  // ids in order
  private ArrayList<String> data; // displayed names in order

  public StudentsModel(final GradeBook book, final int sortedOrder) {
    this.ids = new ArrayList<String>();
    this.data = new ArrayList<String>();

    SortedSet<Student> sortedStudents = new TreeSet<Student>(new Comparator<Student>() {
        public int compare(Student s1, Student s2) {
          if (sortedOrder == GradeBookPanel.BY_ID) {
            // Sort by id
            return s1.getId().compareTo(s2.getId());

          } else if (sortedOrder == GradeBookPanel.BY_NAME) {
            // Sort by last name
            String name1 = s1.getLastName();
            String name2 = s2.getLastName();

            if (name1 == null) {
              name1 = s1.getId();
            }

            if (name2 == null) {
              name2 = s2.getId();
            }

            if (name1.equalsIgnoreCase(name2)) {
              name1 = s1.getFirstName();
              name2 = s2.getFirstName();

              if (name1 == null) {
                name1 = s1.getId();
              }

              if (name2 == null) {
                name2 = s2.getId();
              }

            } else {
              return name1.compareTo(name2);
            }

            if (name1.equalsIgnoreCase(name2)) {
              return s1.getId().compareTo(s2.getId());

            } else {
              return name1.compareTo(name2);
            }

          } else {
            throw new IllegalArgumentException("Unknown sort order: "
                                               + sortedOrder);
          }
        }

        public boolean equals(Object o) {
          return o == this;
        }
      });

    book.forEachStudent(sortedStudents::add);

    for (Student student : sortedStudents) {
      this.ids.add(student.getId());

      if (sortedOrder == GradeBookPanel.BY_ID) {
        // Display id
        this.data.add(student.getId());

      } else if (sortedOrder == GradeBookPanel.BY_NAME) {
        // Display full name
        String name = student.getFullName();
        if (name == null || name.equals("")) {
          name = student.getId();
        }
        this.data.add(name);

      } else {
        throw new IllegalArgumentException("Unknown sort order: "
          + sortedOrder);
      }
    }

  }

  /**
   * Returns the id of the student at the given index
   */
  public String getIdAt(int index) {
    return (String) ids.get(index);
  }

  public Object getElementAt(int index) {
    return data.get(index);
  }

  public int getSize() {
    return this.data.size();
  }

}
