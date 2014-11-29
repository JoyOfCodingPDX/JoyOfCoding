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
import java.util.*;

/**
 * A <code>StudentsList</code> is a <code>JList</code> that lists all
 * of the students in a <code>GradeBook</code> sorted alphabetically
 * by their last name.
 */
@SuppressWarnings("serial")
public class StudentsList extends JPanel {
  private static final int SORT_BY_NAME = 1;
  private static final int SORT_BY_ID = 2;

  private JList list;

  private int howSorted;
  private ArrayList<Student> studentsByName;
  private ArrayList<Student> studentsById;

  

  /**
   * Creates a <code>StudentsList</code> and sets up some initial
   * parameters.
   */
  public StudentsList() {
    this.howSorted = SORT_BY_NAME;

    // Create the JList
    this.list = new JList();
    this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.list.clearSelection();

    // Wrap a ScrollPane around it
    JScrollPane scrollPane = new JScrollPane(this.list);

    // Create the toggle buttons
    JRadioButton byName = new JRadioButton("By Name");
    byName.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          StudentsList.this.howSorted = SORT_BY_NAME;
          StudentsList.this.displayStudents();
        }
      });
    if (this.howSorted == SORT_BY_NAME) {
      byName.setSelected(true);
    }

    JRadioButton byId = new JRadioButton("By ID");
    byId.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          StudentsList.this.howSorted = SORT_BY_ID;
          StudentsList.this.displayStudents();
        }
      });
    if (this.howSorted == SORT_BY_ID) {
      byId.setSelected(true);
    }

    ButtonGroup bg = new ButtonGroup();
    bg.add(byName);
    bg.add(byId);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(byName);
    buttonPanel.add(byId);


    // Add the components to this JPanel
    this.setLayout(new BorderLayout());
    this.add(scrollPane, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Reads the contents of a <code>GradeBook</code> and sorts
   * <code>Student</code>s accordingly.
   */
  public void setGradeBook(GradeBook book) {
    SortedSet<Student> sortedByName = new TreeSet<Student>(new Comparator<Student>() {
        // Sort by last names
        public int compare(Student o1, Student o2) {
          String lastName1 = o1.getLastName();
          String lastName2 = o2.getLastName();

          if (!lastName1.equalsIgnoreCase(lastName2)) {
            return lastName1.compareTo(lastName2);            
          }

          String firstName1 = ((Student) o1).getFirstName();
          String firstName2 = ((Student) o2).getFirstName();

          if (!firstName1.equalsIgnoreCase(firstName2)) {
            return firstName1.compareTo(firstName2);            
          }

          String fullName1 = ((Student) o1).getFullName();
          String fullName2 = ((Student) o2).getFullName();
          return fullName1.compareTo(fullName2);
        }

        public boolean equals(Object o) {
          return true;
        }
      });

    SortedSet<Student> sortedById = new TreeSet<Student>(new Comparator<Student>() {
        // Sort by ids
        public int compare(Student o1, Student o2) {
          String id1 = o1.getId();
          String id2 = o2.getId();
          return id1.compareTo(id2);
        }

        public boolean equals(Object o) {
          return true;
        }
      });

    book.forEachStudent(student -> {
      sortedByName.add(student);
      sortedById.add(student);
    });

    this.studentsByName = new ArrayList<Student>();
    this.studentsByName.addAll(sortedByName);

    this.studentsById = new ArrayList<Student>();
    this.studentsById.addAll(sortedById);

    this.displayStudents();
  }

  /**
   * Returns a <code>ArrayList</code> of the <code>Student</code>s
   * currently being displayed sorted by the currently selected
   * criterion.
   */
  private ArrayList getList() {
    switch(this.howSorted) {
    case SORT_BY_NAME:
      return this.studentsByName;
    case SORT_BY_ID:
      return this.studentsById;
    default:
      return new ArrayList();
    }
  }
  
  /**
   * Adds the students in the current grade book to the list in the
   * appropriate sorted order.
   */
  private void displayStudents() {
    ArrayList list = this.getList();
    Object[] array = new Object[list.size()];

    Iterator iter = list.iterator();
    for (int i = 0; iter.hasNext(); i++) {
      Student student = (Student) iter.next();
      array[i] = student.getFullName() + " (" + student.getId() + ")";
    }
    
    this.list.setListData(array);
    this.list.clearSelection();
  }

  /**
   * Test program that reads a grade book from an XML file and
   * displays the students in it using a <code>StudentsList</code>.
   */    
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("** usage: java StudentsList xmlFile");
      System.exit(1);
    }

    String fileName = args[0];

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

    StudentsList list = new StudentsList();
    list.setGradeBook(book);

    JFrame frame = new JFrame("StudentsList test");
    frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    frame.getContentPane().add(list);
    
    frame.pack();
    frame.setVisible(true);
  }
}
