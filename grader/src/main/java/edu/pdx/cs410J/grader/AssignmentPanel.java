package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * This panel is used to display and edit <code>Assignment</code>s.
 */
@SuppressWarnings("serial")
public class AssignmentPanel extends JPanel {
  private static final String QUIZ = "Quiz";
  private static final String PROJECT = "Project";
  private static final String OTHER = "Other";
  private static final String OPTIONAL = "Optional";
  private static final String POA = "POA";

  static final String DATE_TIME_FORMAT_PATTERN = "M/d/yyyy h:mm a";
  static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_PATTERN);

  // GUI components we care about
  private JTextField nameField;
  private JComboBox<String> typeBox;
  private JTextField pointsField;
  private JTextField descriptionField;
  private JTextField dueDateField;
  private NotesPanel notes;

  /**
   * Creates and adds GUI components to a new
   * <code>AssignmentPanel</code>.
   */
  public AssignmentPanel(boolean canCreate) {
    this.setLayout(new BorderLayout());

    // Panel containing information about an assignment
    JPanel infoPanel = new JPanel();
    infoPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
    infoPanel.setLayout(new BorderLayout());

    addAssignmentInformationWidgets(infoPanel);

    this.add(infoPanel, BorderLayout.NORTH);

    if (!canCreate) {
      this.nameField.setEditable(false);
    }

    // Add a NotePanel
    this.notes = new NotesPanel();
    this.notes.setNotable(new Notable() {
        private ArrayList<String> notes = new ArrayList<>();

        @Override
        public java.util.List<String> getNotes() {
          return notes;
        }

        @Override
        public void addNote(String note) {
          notes.add(note);
        }

        @Override
        public void removeNote(String note) {
          notes.remove(note);
        }
      });
    this.add(notes, BorderLayout.CENTER);
  }

  private void addAssignmentInformationWidgets(JPanel infoPanel) {
    JPanel labels = new JPanel();
    labels.setLayout(new GridLayout(0, 1));
    labels.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
    labels.add(new JLabel("Name:"));
    labels.add(new JLabel("Max points:"));
    labels.add(new JLabel("Type:"));
    labels.add(new JLabel("Description:"));
    labels.add(new JLabel("Due date:"));

    JPanel fields = new JPanel();
    fields.setLayout(new GridLayout(0, 1));
    this.nameField = new JTextField(8);
    fields.add(this.nameField);
    this.pointsField = new JTextField(5);
    fields.add(this.pointsField);

    addTypeBox(fields);

    this.descriptionField = new JTextField(20);
    fields.add(this.descriptionField);
    this.dueDateField = new JTextField(22);
    fields.add(this.dueDateField);

    infoPanel.add(labels, BorderLayout.WEST);
    infoPanel.add(fields, BorderLayout.CENTER);
  }

  private void addTypeBox(JPanel fields) {
    this.typeBox = new JComboBox<>();
    this.typeBox.addItem(QUIZ);
    this.typeBox.addItem(PROJECT);
    this.typeBox.addItem(POA);
    this.typeBox.addItem(OTHER);
    this.typeBox.addItem(OPTIONAL);
    fields.add(this.typeBox);
  }

  /**
   * Creates a new <code>Assignment</code> based on the contents of
   * this <code>AssignmentPanel</code>.
   */
  public Assignment createAssignment() {
    // Get the name and max points of the assignment
    String name = nameField.getText();
    if (isNullOrEmpty(name)) {
      return error("No assignment name specified");
    }

    String points = pointsField.getText();
    if (isNullOrEmpty(points)) {
      return error("No points value specified");
    }

    // Create a new Assignment object
    try {
      double d = Double.parseDouble(points);
      Assignment newAssign = new Assignment(name, d);
      this.notes.getNotable().getNotes().forEach(newAssign::addNote);

      updateAssignment(newAssign);
      this.notes.setNotable(newAssign);
      return newAssign;
      
    } catch (NumberFormatException ex) {
      return error(points + " is not a number");
    }    
  }

  private boolean isNullOrEmpty(String name) {
    return name == null || name.equals("");
  }

  private Assignment error(String message) {
    JOptionPane.showMessageDialog(AssignmentPanel.this,
      new String[]{message},
      "Error",
      JOptionPane.ERROR_MESSAGE);
    return null;
  }

  /**
   * Displays the contents of a given <code>Assignment</code> in this
   * <code>AssignmentPanel</code>.
   */
  public void displayAssignment(Assignment assign) {
    this.nameField.setText(assign.getName());
    this.pointsField.setText(String.valueOf(assign.getPoints()));
    Assignment.AssignmentType type = assign.getType();
    if (type == Assignment.AssignmentType.QUIZ) {
      this.typeBox.setSelectedItem(QUIZ);

    } else if (type == Assignment.AssignmentType.PROJECT) {
      this.typeBox.setSelectedItem(PROJECT);

    } else if (type == Assignment.AssignmentType.OTHER) {
      this.typeBox.setSelectedItem(OTHER);

    } else if (type == Assignment.AssignmentType.OPTIONAL) {
      this.typeBox.setSelectedItem(OPTIONAL);

    } else if (type == Assignment.AssignmentType.POA) {
      this.typeBox.setSelectedItem(POA);

    } else {
      String s = "Invalid assignment type: " + type;
      throw new IllegalArgumentException(s);
    }

    String description = assign.getDescription();
    if (isNotEmpty(description)) {
      this.descriptionField.setText(description);

    } else {
      this.descriptionField.setText("");
    }

    LocalDateTime dueDate = assign.getDueDate();
    if (dueDate != null) {
      this.dueDateField.setText(DATE_TIME_FORMAT.format(dueDate));

    } else {
      this.dueDateField.setText("");
    }

    this.notes.setNotable(assign);
  }

  /**
   * Fills in the contents of an <code>Assignment</code> based on the
   * contents of this <code>AssignmentPanel</code>.
   */
  public void updateAssignment(Assignment assign) {
    String points = pointsField.getText();
    if (isNullOrEmpty(points)) {
      error("No points value specified");
      return;
    }

    try {
      double d = Double.parseDouble(points);
      assign.setPoints(d);
      
    } catch (NumberFormatException ex) {
      error(points + " is not a number");
      return;
    }

    setAssignmentType(assign);

    String description = this.descriptionField.getText();

    if (isNotEmpty(description)) {
      assign.setDescription(description);
    }

    String dueDate = this.dueDateField.getText();
    if (isNotEmpty(dueDate)) {
      try {
        assign.setDueDate(LocalDateTime.parse(dueDate.trim(), DATE_TIME_FORMAT));

      } catch (DateTimeParseException ex) {
        error(dueDate + " is not a validate date (" + DATE_TIME_FORMAT_PATTERN + ")");
      }

    } else {
      assign.setDueDate(null);
    }

    // Adding notes is taken care of by the NotesPanel
  }

  private void setAssignmentType(Assignment assign) {
    String type = (String) this.typeBox.getSelectedItem();

    switch (type) {
      case QUIZ:
        assign.setType(Assignment.AssignmentType.QUIZ);
        break;
      case PROJECT:
        assign.setType(Assignment.AssignmentType.PROJECT);
        break;
      case OTHER:
        assign.setType(Assignment.AssignmentType.OTHER);
        break;
      case OPTIONAL:
        assign.setType(Assignment.AssignmentType.OPTIONAL);
        break;
      case POA:
        assign.setType(Assignment.AssignmentType.POA);
        break;
      default:
        String s = "Unknown assignment type: " + type;
        throw new IllegalArgumentException(s);
    }
  }

  private boolean isNotEmpty(String description) {
    return description != null && !description.equals("");
  }

  /**
   * Test program
   */
  public static void main(String[] args) {
    String fileName = args[0];
    String assignName = args[1];

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

    Assignment assign = book.getAssignment(assignName);
    if (assign == null) {
      System.err.println("Not such assignment: " + assignName);
      System.exit(1);
    }

    AssignmentPanel assignPanel = new AssignmentPanel(false);
    assignPanel.displayAssignment(assign);

    JFrame frame = new JFrame("AssignmentPanel test");
    frame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
          System.exit(1);
        }
      });

    frame.getContentPane().add(assignPanel);
    
    frame.pack();
    frame.setVisible(true);
  }
}
