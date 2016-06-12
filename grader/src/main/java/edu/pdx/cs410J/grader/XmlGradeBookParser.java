package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static edu.pdx.cs410J.grader.GradeBook.LetterGradeRanges.LetterGradeRange;

/**
 * This class creates a <code>GradeBook</code> from the contents of an
 * XML file.
 */
public class XmlGradeBookParser extends XmlHelper {
  private GradeBook book;    // The grade book we're creating
  private InputStream in;    // Input source of grade book
  private File studentDir;   // Where the student XML file live

  /**
   * Creates an <code>XmlGradeBookParser</code> that creates a
   * <code>GradeBook</code> from a file of a given name.
   */
  public XmlGradeBookParser(String fileName) throws IOException {
    this(new File(fileName));
  }

  /**
   * Creates an <code>XmlGradeBookParser</code> that creates a
   * <code>GradeBook</code> from the contents of a <code>File</code>.
   */
  public XmlGradeBookParser(File file) throws IOException {
    this(new FileInputStream(file));
    this.setStudentDir(file.getCanonicalFile().getParentFile());
  }

  /**
   * Creates an <code>XmlGradeBookParser</code> that creates a
   * <code>GradeBook</code> from the contents of an
   * <code>InputStream</code>.
   */
  XmlGradeBookParser(InputStream in) {
    this.in = in;
  }

  /**
   * Sets the directory in which the XML files for students are
   * generated.
   */
  public void setStudentDir(File dir) {
    if (!dir.exists()) {
      throw new IllegalArgumentException(dir + " does not exist");
    }

    if (!dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }

    this.studentDir = dir;
  }

  /**
   * Extracts an <code>Assignment</code> from an <code>Element</code>
   */
  private static Assignment extractAssignmentFrom(Element element) 
    throws ParserException {
    Assignment assign = null;

    String name = null;
    String description = null;

    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("name")) {
        name = extractTextFrom(child);

      } else if (child.getTagName().equals("description")) {
        description = extractTextFrom(child);

      } else if (child.getTagName().equals("points")) {
        String points = extractTextFrom(child);
        try {
          if (name == null) {
            throw new ParserException("No name for assignment with " +
              "points " + points);
          }
          assign = new Assignment(name, Double.parseDouble(points));
          if (description != null) {
            assign.setDescription(description);
          }

        } catch (NumberFormatException ex) {
          throw new ParserException("Invalid points value: " +
            points);
        }

      } else if (child.getTagName().equals("due-date")) {
        String dueDate = extractTextFrom(child);
        try {
          assign.setDueDate(LocalDateTime.parse(dueDate, DATE_TIME_FORMAT));

        } catch(DateTimeParseException ex) {
          throw new ParserException("Invalidate LocalDateTime: " + dueDate, ex);
        }

      } else if (child.getTagName().equals("notes")) {
        for (String note : extractNotesFrom(child)) {
          assign.addNote(note);
        }
      }
    }

    if (assign == null ) {
      throw new ParserException("No assignment found!");
    }

    setAssignmentTypeFromXml(element, assign);

    return assign;
  }

  private static void setAssignmentTypeFromXml(Element assignmentElement, Assignment assign) throws ParserException {
    String type = assignmentElement.getAttribute("type");
    switch (type) {
      case "PROJECT":
        assign.setType(Assignment.AssignmentType.PROJECT);
        break;
      case "QUIZ":
        assign.setType(Assignment.AssignmentType.QUIZ);
        break;
      case "OTHER":
        assign.setType(Assignment.AssignmentType.OTHER);
        break;
      case "OPTIONAL":
        assign.setType(Assignment.AssignmentType.OPTIONAL);
        break;
      case "POA":
        assign.setType(Assignment.AssignmentType.POA);
        break;
      default:
        throw new ParserException("Unknown assignment type: " + type);
    }
  }

  /**
   * Parses the source and from it creates a <code>GradeBook</code>.
   */
  public GradeBook parse() throws ParserException {
    Document doc = parseDocumentFromInputStream();


    Element root = null;
    if (doc != null) {
      root = doc.getDocumentElement();
    }
    if (doc == null || root == null) {
      throw new ParserException("Document parsing failed");
    }

    NodeList children = root.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if (!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if (child.getTagName().equals("name")) {
        this.book = new GradeBook(extractTextFrom(child));
	      this.book.setDirty(false);

      } else if (this.book == null) {
        throw new ParserException("name element is not first");

      } else if (child.getTagName().equals("assignments")) {
        extractAssignmentsFrom(child);

      } else if (child.getTagName().equals("letter-grade-ranges")) {
        extractLetterGradeRangesFrom(child);

      } else if (child.getTagName().equals("students")) {
        extractStudentsFrom(child);

      } else if (child.getTagName().equals("lateDays")) {
        // Fill in later, maybe.
      }
    }

    if (this.book != null) {
      // The book is initially clean
      this.book.makeClean();
    }

    return this.book;
  }

  private void extractLetterGradeRangesFrom(Element parent) {
    Student.Section section;
    String attributeName = "for-section";
    if (parent.hasAttribute(attributeName)) {
      String value = parent.getAttribute(attributeName);
      section = Student.Section.fromString(value);

    } else {
      section = Student.Section.UNDERGRADUATE;
    }

    NodeList ranges = parent.getChildNodes();
    for (int j = 0; j < ranges.getLength(); j++) {
      Node range = ranges.item(j);

      if (!(range instanceof Element)) {
        continue;
      }

      extractLetterGradeRangeFrom((Element) range, section);
    }
  }

  private void extractLetterGradeRangeFrom(Element element, Student.Section section) {
    String letterGradeString = element.getAttribute("letter-grade");
    LetterGrade letterGrade = LetterGrade.fromString(letterGradeString);
    LetterGradeRange range = this.book.getLetterGradeRanges(section).getRange(letterGrade);
    range.setRange(toInt(element.getAttribute("minimum-score")), toInt(element.getAttribute("maximum-score")));

  }

  private int toInt(String value) {
    return Integer.parseInt(value);
  }

  private void extractStudentsFrom(Element child) throws ParserException {
    NodeList students = child.getChildNodes();
    for (int j = 0; j < students.getLength(); j++) {
      Node student = students.item(j);

      if (!(student instanceof Element)) {
        continue;
      }

      Element idElement = (Element) student;
      if (idElement.getTagName().equals("id")) {
        String id = extractTextFrom(idElement);
            // Locate the XML file for the Student
        File file =
          new File(this.studentDir, id + ".xml");
        if (!file.exists()) {
          throw new IllegalArgumentException("No XML file for " +
                                             id);
        }

        try {
          XmlStudentParser sp = new XmlStudentParser(file);
          Student stu = sp.parseStudent();
          this.book.addStudent(stu);

        } catch (Exception ex) {
          String s = "While parsing " + file + ": " + ex;
          throw new ParserException(s);
        }
      }
    }
  }

  private void extractAssignmentsFrom(Element child) throws ParserException {
    NodeList assignments = child.getChildNodes();
    for (int j = 0; j < assignments.getLength(); j++) {
      Node assignment = assignments.item(j);
      if (assignment instanceof Element) {
        Assignment assign =
          extractAssignmentFrom((Element) assignment);
        this.book.addAssignment(assign);
      }
    }
  }

  private Document parseDocumentFromInputStream() throws ParserException {
    // Parse the source
    Document doc;

    // Create a DOM tree from the XML source
    try {
      DocumentBuilderFactory factory =
	DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder =
	factory.newDocumentBuilder();
      builder.setErrorHandler(this);
      builder.setEntityResolver(this);

      doc = builder.parse(new InputSource(this.in));

    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new ParserException("While parsing XML source: " + ex);

    }
    return doc;
  }

}
