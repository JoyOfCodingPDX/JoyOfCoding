package edu.pdx.cs399J.grader;

import edu.pdx.cs399J.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * This panel displays the contents of a grade book.  On the left-hand
 * side of the panel is a list of the students in the class.  In the
 * center of the panel is a tabbed pane that lets you view/edit
 * information about the class, a student, or a student's grades.
 */
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

    this.add(split);
  }

  /**
   * Displays information about a given student
   */
  private void displayStudent(String id) {
    Student student = this.book.getStudent(id);
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
class StudentsModel extends AbstractListModel {

  private ArrayList ids;  // ids in order
  private ArrayList data; // displayed names in order

  public StudentsModel(final GradeBook book, final int sortedOrder) {
    this.ids = new ArrayList();
    this.data = new ArrayList();

    SortedSet sortedStudents = new TreeSet(new Comparator() {
        public int compare(Object o1, Object o2) {
          Student s1 = (Student) o1;
          Student s2 = (Student) o2;

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

            if (name1.equals(name2)) {
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

            if (name1.equals(name2)) {
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

    Iterator iter = book.getStudentIds().iterator();
    while (iter.hasNext()) {
      String id = (String) iter.next();
      sortedStudents.add(book.getStudent(id));
    }

    iter = sortedStudents.iterator();
    while (iter.hasNext()) {
      Student student = (Student) iter.next();
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
