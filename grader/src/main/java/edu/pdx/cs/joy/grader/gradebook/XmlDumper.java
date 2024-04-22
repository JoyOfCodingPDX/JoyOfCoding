package edu.pdx.cs.joy.grader.gradebook;

import edu.pdx.cs.joy.grader.gradebook.Assignment.ProjectType;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static edu.pdx.cs.joy.grader.gradebook.GradeBook.LetterGradeRanges.LetterGradeRange;

/**
 * This class dumps the contents of a <code>GradeBook</code> to an XML
 * file.  By default, the students in the grade book are dumped to XML
 * files in the same directory as the grade book's XML file.
 *
 * @author David Whitlock
 * @since Fall 2000
 */
public class XmlDumper extends XmlHelper {

  private File studentDir = null; // Where to dump student XML files
  PrintWriter pw;          // Where to dump grade book

  /**
   * Creates a new <code>XmlDumper</code> that dumps the contents of a
   * grade book to a given file.
   */
  public XmlDumper(File xmlFile) throws IOException {
    this(new PrintWriter(new FileWriter(xmlFile), true));

    if (!xmlFile.exists()) {
      if (!xmlFile.createNewFile()) {
        throw new IOException("Could not create file " + xmlFile);
      }
    }

    this.setStudentDir(xmlFile.getCanonicalFile().getParentFile());
  }

  /**
   * Creates a new <code>XmlDumper</code> that dumps the contents of a
   * grade book to a file of the given name.
   */
  public XmlDumper(String xmlFileName) throws IOException {
    this(new File(xmlFileName));
  }

  /**
   * Creates an <code>XmlDumper</code> that dumps the contents of a
   * grade book to a <code>PrintWriter</code>.  The location of the
   * student files is unspecified.
   */
  private XmlDumper(PrintWriter pw) {
    this.pw = pw;
  }

  /**
   * Sets the directory in which the XML files for students are
   * generated.
   */
  public void setStudentDir(File dir) {
    if (dir.exists() && !dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }
    
    this.studentDir = dir;
  }

  /**
   * Dumps the contents of a <code>GradeBook</code> in XML format.
   */
  public void dump(GradeBook book) throws IOException {
    Document doc = dumpGradeBook(book, this);

    try {
      writeXmlToPrintWriter(doc, this.pw);

    } catch (TransformerException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    dumpDirtyStudents(book);

    // Mark the grade book as being clean
    book.makeClean();
  }

  static Document dumpGradeBook(GradeBook book, XmlHelper helper) {
    Document doc = createDocumentForGradeBook(helper);

    Element root = doc.getDocumentElement();

    appendXmlForClassName(book, root);
    appendXmlForAssignments(book, doc, root);
    appendXmlForLetterGradeRanges(book, doc, root);
    appendXmlForSectionNames(book, doc, root);
    appendXmlForStudents(book, doc, root);

    return doc;
  }

  private static void appendXmlForSectionNames(GradeBook book, Document doc, Element root) {
    for (Student.Section section : book.getSections()) {
      String sectionName = book.getSectionName(section);
      if (sectionName != null) {
        root.appendChild(appendXmlForSectionName(root, section, sectionName));
      }
    }
  }

  private static Element appendXmlForSectionName(Element parent, Student.Section section, String sectionName) {
    Element sectionNameNode = appendTextElementIfValueIsNotNull(parent, "section-name", sectionName);
    sectionNameNode.setAttribute("for-section", getSectionXmlAttributeValue(section));
    return sectionNameNode;
  }

  private static void appendXmlForLetterGradeRanges(GradeBook book, Document doc, Element root) {
    for (Student.Section section : book.getSections()) {
      Element lgrNode = appendXmlForLetterGradeRange(book, section, doc);
      root.appendChild(lgrNode);
    }
  }

  private static Element appendXmlForLetterGradeRange(GradeBook book, Student.Section section, Document doc) {
    Element lgrNode = doc.createElement("letter-grade-ranges");
    lgrNode.setAttribute("for-section", getSectionXmlAttributeValue(section));
    for (LetterGradeRange range : book.getLetterGradeRanges(section)) {
      appendXmlForLetterGradeRange(range, doc, lgrNode);
    }
    return lgrNode;
  }

  private static String getSectionXmlAttributeValue(Student.Section section) {
    return Objects.toString(section, null);
  }

  private static void appendXmlForLetterGradeRange(LetterGradeRange range, Document doc, Element parent) {
    Element node = doc.createElement("letter-grade-range");
    node.setAttribute("letter-grade", range.letterGrade().asString());
    node.setAttribute("minimum-score", String.valueOf(range.minimum()));
    node.setAttribute("maximum-score", String.valueOf(range.maximum()));

    parent.appendChild(node);
  }

  private static Document createDocumentForGradeBook(XmlHelper helper) {
    Document doc = null;

    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(helper);
      builder.setEntityResolver(helper);

      DOMImplementation dom =
        builder.getDOMImplementation();
      DocumentType docType =
        dom.createDocumentType("gradebook", publicID, systemID);
      doc = dom.createDocument(null, "gradebook", docType);

    } catch (ParserConfigurationException | DOMException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);

    }
    return doc;
  }

  private static void appendXmlForStudents(GradeBook book, Document doc, Element root) {
    // Students
    Element studentsNode = doc.createElement("students");
    for (String id : book.getStudentIds()) {
      appendTextElementIfValueIsNotNull(studentsNode, "id", id);
    }

    root.appendChild(studentsNode);
  }

  private static void appendXmlForClassName(GradeBook book, Element root) {
    appendTextElementIfValueIsNotNull(root, "name", book.getClassName());
  }

  private static void appendXmlForAssignments(GradeBook book, Document doc, Element root) {
    // assignment nodes
    Element assignments = doc.createElement("assignments");
    for (String assignmentName : book.getAssignmentNames()) {
      Assignment assign = book.getAssignment(assignmentName);
      Element assignNode = doc.createElement("assignment");

      setAssignmentTypeAttribute(assign, assignNode);
      appendTextElementIfValueIsNotNull(assignNode, "name", assign.getName());
      appendTextElementIfValueIsNotNull(assignNode, "description", assign.getDescription());
      appendTextElementIfValueIsNotNull(assignNode, "points", String.valueOf(assign.getPoints()));
      appendTextElementIfValueIsNotNull(assignNode, "canvas-id", String.valueOf(assign.getCanvasId()));
      appendTextElementIfValueIsNotNull(assignNode, "due-date", assign.getDueDate());

      doNotes(doc, assignNode, assign.getNotes());

      assignments.appendChild(assignNode);

    }
    root.appendChild(assignments);
  }

  private static void appendTextElementIfValueIsNotNull(Element parent, String elementName, LocalDateTime dateTime) {
    if (dateTime != null) {
      appendTextElementIfValueIsNotNull(parent, elementName, dateTime.format(DATE_TIME_FORMAT));
    }
  }

  private static void setAssignmentTypeAttribute(Assignment assignment, Element assignmentNode) {
    Assignment.AssignmentType type = assignment.getType();
    switch (type) {
      case PROJECT:
        assignmentNode.setAttribute("type", "PROJECT");
        setProjectTypeAttribute(assignment, assignmentNode);
        break;

      case QUIZ:
        assignmentNode.setAttribute("type", "QUIZ");
        break;

      case OTHER:
        assignmentNode.setAttribute("type", "OTHER");
        break;

      case OPTIONAL:
        assignmentNode.setAttribute("type", "OPTIONAL");
        break;

      case POA:
        assignmentNode.setAttribute("type", "POA");
        break;

      default:
        throw new IllegalArgumentException("Can't handle assignment " +
          "type " + type);
    }
  }

  private static void setProjectTypeAttribute(Assignment assignment, Element assignmentNode) {
    ProjectType projectType = assignment.getProjectType();
    if (projectType != null) {
      switch (projectType) {
        case APP_CLASSES:
          assignmentNode.setAttribute("project-type", "APP_CLASSES");
          break;

        case TEXT_FILE:
          assignmentNode.setAttribute("project-type", "TEXT_FILE");
          break;

        case PRETTY_PRINT:
          assignmentNode.setAttribute("project-type", "PRETTY_PRINT");
          break;

        case KOANS:
          assignmentNode.setAttribute("project-type", "KOANS");
          break;

        case XML:
          assignmentNode.setAttribute("project-type", "XML");
          break;

        case REST:
          assignmentNode.setAttribute("project-type", "REST");
          break;

        case ANDROID:
          assignmentNode.setAttribute("project-type", "ANDROID");
          break;

        default:
          throw new IllegalStateException("Can't handle project type: " + projectType);
      }
    }

  }

  private void dumpDirtyStudents(GradeBook book) {
    book.forEachStudent(student -> {
      if (student.isDirty()) {
        dumpStudent(student);
      }
    });
  }

  /**
   * Creates a <code>notes</code> XML element for a given
   * <code>List</code> of notes.
   */
  private static void doNotes(Document doc, Element parent, List<String> notes) {
    Element notesNode = doc.createElement("notes");
    for (String note : notes) {
      appendTextElementIfValueIsNotNull(notesNode, "note", note);
    }

    parent.appendChild(notesNode);
  }

  /**
   * Dumps a <code>Student</code> out to an XML file whose name is
   * based on the student's id and resides in the
   * <code>studentDir</code>.
   */
  private void dumpStudent(Student student) {
    Document doc = toXml(student);

    // Now dump DOM tree to the file
    File studentFile = new File(studentDir, student.getId() + ".xml");
    PrintWriter pw = new PrintWriter(newFileWriter(studentFile), true);

    try {
      writeXmlToPrintWriter(doc, pw);

    } catch (TransformerException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
  }

  private FileWriter newFileWriter(File studentFile) {
    try {
      return new FileWriter(studentFile);

    } catch (IOException ex) {
      throw new IllegalStateException("Couldn't create FileWriter for " + studentFile, ex);
    }
  }

  /**
   * Returns a DOM tree that represents a <code>Student</code>
   */
  public static Document toXml(Student student) {
    Document doc = createXmlDocument();

    Element root = doc.getDocumentElement();

    appendStudentInformation(student, root);
    appendGradesInformation(student, root);
    appendLateInformation(student, root);
    appendResubmittedInformation(student, root);
    appendNotes(student, root);

    return doc;
  }

  private static void appendNotes(Student student, Element parent) {
    List<String> notes = student.getNotes();
    if (!notes.isEmpty()) {
      doNotes(parent.getOwnerDocument(), parent, notes);
    }
  }

  private static void appendResubmittedInformation(Student student, Element parent) {
    Document doc = parent.getOwnerDocument();
    List<String> resubmitted = student.getResubmitted();
    if (!resubmitted.isEmpty()) {
      Element resubNode = doc.createElement("resubmitted");

      for (String assignmentName : resubmitted) {
        appendTextElementIfValueIsNotNull(resubNode, "name", assignmentName);
      }

      parent.appendChild(resubNode);
    }
  }

  private static void appendLateInformation(Student student, Element parent) {
    Document doc = parent.getOwnerDocument();
    List<String> late = student.getLate();
    if (!late.isEmpty()) {
      Element lateNode = doc.createElement("late");

      for (String assignmentName : late) {
        appendTextElementIfValueIsNotNull(lateNode, "name", assignmentName);
      }

      parent.appendChild(lateNode);
    }
  }

  private static void appendGradesInformation(Student student, Element parent) {
    Document doc = parent.getOwnerDocument();
    Iterator<String> gradeNames = student.getGradeNames().iterator();
    if (gradeNames.hasNext()) {
      Element gradesNode = doc.createElement("grades");
      while (gradeNames.hasNext()) {
        String gradeName = gradeNames.next();
        Grade grade = student.getGrade(gradeName);

        Element gradeNode = doc.createElement("grade");

        appendTextElementIfValueIsNotNull(gradeNode, "name", grade.getAssignmentName());
        appendTextElementIfValueIsNotNull(gradeNode, "score", String.valueOf(grade.getScore()));

        appendSubmissionsInformation(grade.getSubmissionInfos(), gradeNode);

        doNotes(doc, gradeNode, grade.getNotes());

        if (grade.getScore() == Grade.INCOMPLETE) {
          gradeNode.setAttribute("type", "INCOMPLETE");

        } else if (grade.getScore() == Grade.NO_GRADE) {
          gradeNode.setAttribute("type", "NO_GRADE");
        }

        gradesNode.appendChild(gradeNode);
      }

      parent.appendChild(gradesNode);
    }
  }

  private static void appendSubmissionsInformation(List<Grade.SubmissionInfo> submissionInfos, Element parent) {
    if (!submissionInfos.isEmpty()) {
      Document doc = parent.getOwnerDocument();
      Element submissions = doc.createElement("submissions");
      parent.appendChild(submissions);

      submissionInfos.forEach(info -> {
        Element submissionInfo = doc.createElement("submission-info");
        if (info.isLate()) {
          submissionInfo.setAttribute("late", "true");
        }
        submissions.appendChild(submissionInfo);
        appendSubmissionInformation(info, submissionInfo);
      });
    }

  }

  private static void appendSubmissionInformation(Grade.SubmissionInfo info, Element parent) {
    appendTextElementIfValueIsNotNull(parent, "date", info.getSubmissionTime());
    appendTextElementIfValueIsNotNull(parent, "estimated-hours", info.getEstimatedHours());
  }

  private static void appendStudentInformation(Student student, Element root) {
    appendTextElementIfValueIsNotNull(root, "id", student.getId());
    appendTextElementIfValueIsNotNull(root, "firstName", student.getFirstName());
    appendTextElementIfValueIsNotNull(root, "lastName", student.getLastName());
    appendTextElementIfValueIsNotNull(root, "nickName", student.getNickName());
    appendTextElementIfValueIsNotNull(root, "email", student.getEmail());
    appendTextElementIfValueIsNotNull(root, "major", student.getMajor());
    appendTextElementIfValueIsNotNull(root, "canvas-id", student.getCanvasId());
    appendTextElementIfValueIsNotNull(root, "letter-grade", Objects.toString(student.getLetterGrade(), null));

    setAttributeIfValueIsNotNull(root, "enrolled-section", getSectionXmlAttributeValue(student.getEnrolledSection()));
  }

  private static void setAttributeIfValueIsNotNull(Element root, String name, String value) {
    if (value != null) {
      root.setAttribute(name, value);
    }
  }

  private static Document createXmlDocument() {
    Document doc = null;

    try {
      DocumentBuilderFactory factory =
        DocumentBuilderFactory.newInstance();
      factory.setValidating(true);

      DocumentBuilder builder = factory.newDocumentBuilder();

      DOMImplementation dom =
        builder.getDOMImplementation();
      DocumentType docType =
        dom.createDocumentType("student", publicID, systemID);
      doc = dom.createDocument(null, "student", docType);

    } catch (ParserConfigurationException | DOMException ex) {
      ex.printStackTrace(System.err);
      System.exit(1);
    }
    return doc;
  }

  private static Element appendTextElementIfValueIsNotNull(Element parent, String elementName, Object textValue) {
    if (textValue != null) {
      Document doc = parent.getOwnerDocument();
      Element id = doc.createElement(elementName);
      id.appendChild(doc.createTextNode(String.valueOf(textValue)));
      parent.appendChild(id);
      return id;

    } else {
      return null;
    }
  }

}
