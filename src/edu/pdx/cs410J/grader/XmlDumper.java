package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import org.apache.xerces.dom.*;
import org.apache.xml.serialize.*;
import org.w3c.dom.*;

/**
 * This class dumps the contents of a <code>GradeBook</code> to an XML
 * file.  By default, the students in the grade book are dumped to XML
 * files in the same directory as the grade book's XML file.
 */
public class XmlDumper {
  private static final String systemID = 
    "http://www.cs.pdx.edu/~whitlock/dtds/gradebook.dtd";
  private static final String publicID = 
    "-//Portland State University//DTD CS410J Grade Book//EN";

  private File studentDir = null; // Where to dump student XML files
  PrintWriter pw = null;          // Where to dump grade book

  /**
   * Creates a new <code>XmlDumper</code> that dumps the contents of a
   * grade book to a given file.
   */
  public XmlDumper(File xmlFile) throws IOException {
    this(new PrintWriter(new FileWriter(xmlFile), true));

    if(!xmlFile.exists()) {
      xmlFile.createNewFile();
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
    if(dir.exists() && !dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }
    
    this.studentDir = dir;
  }

  /**
   * Dumps the contents of a <code>GradeBook</code> in XML format.
   */
  public void dump(GradeBook book) throws IOException {
    Document doc = null;

    try {
      DOMImplementation dom =
        DOMImplementationImpl.getDOMImplementation();
      DocumentType dtd = 
        dom.createDocumentType("gradebook", publicID, systemID);
      doc = dom.createDocument(null, "gradebook", dtd);

    } catch(DOMException ex) {
      // Eep, this is bad
      ex.printStackTrace(System.err);
      System.exit(1);
    }
    
    Element root = doc.getDocumentElement();
    
    // name node
    Element name = doc.createElement("name");
    name.appendChild(doc.createTextNode(book.getClassName()));
    root.appendChild(name);

    // assignment nodes
    Element assignments = doc.createElement("assignments");
    Iterator iter = book.getAssignmentNames().iterator();
    while(iter.hasNext()) {
      Assignment assign = book.getAssignment((String) iter.next());
      Element assignNode = doc.createElement("assignment");

      Element assignName = doc.createElement("name");
      assignName.appendChild(doc.createTextNode(assign.getName()));
      assignNode.appendChild(assignName);

      String desc = assign.getDescription();
      if(desc != null) {
        Element assignDesc = doc.createElement("description");
        assignDesc.appendChild(doc.createTextNode(desc));
        assignNode.appendChild(assignDesc);
      }

      Element assignPoints = doc.createElement("points");
      assignPoints.appendChild(doc.createTextNode("" +
                                                  assign.getPoints()));
      assignNode.appendChild(assignPoints);

      doNotes(doc, assignNode, assign.getNotes());

      assignments.appendChild(assignNode);

      int type = assign.getType();
      switch(type) {
      case Assignment.PROJECT:
        assignNode.setAttribute("type", "PROJECT");
        break;

      case Assignment.QUIZ:
        assignNode.setAttribute("type", "QUIZ");
        break;

      case Assignment.OTHER:
        assignNode.setAttribute("type", "OTHER");
        break;

      default: 
        throw new IllegalArgumentException("Can't handle assignment " +
                                           "type " + type);
      }
    }
    root.appendChild(assignments);


    // Students
    Element studentsNode = doc.createElement("students");
    Iterator ids = book.getStudentIds().iterator();
    while(ids.hasNext()) {
      String id = (String) ids.next();
      Element studentNode = doc.createElement("id");
      studentNode.appendChild(doc.createTextNode(id));

      studentsNode.appendChild(studentNode);

      Student student = book.getStudent(id);
      if(student.isDirty()) {
        dumpStudent(student);
      }
    }

    root.appendChild(studentsNode);

    // Finally serialize the DOM tree to a Writer
    OutputFormat format = new OutputFormat(doc);
    format.setIndenting(true);
    format.setIndent(2);
    format.setLineWidth(70);
    
    XMLSerializer serial = new XMLSerializer(this.pw, format);
    serial.asDOMSerializer();
    serial.serialize(doc);
  }

  /**
   * Creates a <code>notes</code> XML element for a given
   * <code>List</code> of notes.
   */
  private static void doNotes(Document doc, Element parent, List notes) {
    Element notesNode = doc.createElement("notes");
    for(int i = 0; i < notes.size(); i++) {
      String note = (String) notes.get(i);
      Element noteNode = doc.createElement("note");
      noteNode.appendChild(doc.createTextNode(note));

      notesNode.appendChild(noteNode);
    }

    parent.appendChild(notesNode);
  }

  /**
   * Dumps a <code>Student</code> out to an XML file whose name is
   * based on the student's id and resides in the
   * <code>studentDir</code>.
   */
  private void dumpStudent(Student student) throws IOException {
    Document doc = toXml(student);

    // Now dump DOM tree to the file
    File studentFile = new File(studentDir, student.getId() + ".xml");
    PrintWriter pw = new PrintWriter(new FileWriter(studentFile), true);

    OutputFormat format = new OutputFormat(doc);
    format.setIndenting(true);
    format.setIndent(2);
    format.setLineWidth(70);
    
    XMLSerializer serial = new XMLSerializer(pw, format);
    serial.asDOMSerializer();
    serial.serialize(doc);  
  }

  /**
   * Returns a DOM tree that represents a <code>Student</code>
   */
  static Document toXml(Student student) {
    // Create a new Document for the Student
    Document doc = null;

    try {
      DOMImplementation dom =
        DOMImplementationImpl.getDOMImplementation();
      DocumentType dtd = 
        dom.createDocumentType("student", publicID, systemID);
      doc = dom.createDocument(null, "student", dtd);

    } catch(DOMException ex) {
      // Eep, this is bad
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    Element root = doc.getDocumentElement();

    // id node
    Element id = doc.createElement("id");
    id.appendChild(doc.createTextNode(student.getId()));
    root.appendChild(id);

    // First name
    if(student.getFirstName() != null) {
      Element firstName = doc.createElement("firstName");
      firstName.appendChild(doc.createTextNode(student.getFirstName()));
      root.appendChild(firstName);
    }

    if(student.getLastName() != null) {
      Element lastName = doc.createElement("lastName");
      lastName.appendChild(doc.createTextNode(student.getLastName()));
      root.appendChild(lastName);
    }

    if(student.getNickName() != null) {
      Element nickName = doc.createElement("nickName");
      nickName.appendChild(doc.createTextNode(student.getNickName()));
      root.appendChild(nickName);
    }

    if(student.getEmail() != null) {
      Element email = doc.createElement("email");
      email.appendChild(doc.createTextNode(student.getEmail()));
      root.appendChild(email);
    }

    if(student.getSsn() != null) {
      Element ssn = doc.createElement("ssn");
      ssn.appendChild(doc.createTextNode(student.getSsn()));
      root.appendChild(ssn);
    }

    if(student.getMajor() != null) {
      Element major = doc.createElement("major");
      major.appendChild(doc.createTextNode(student.getMajor()));
      root.appendChild(major);
    }

    Iterator gradeNames = student.getGradeNames().iterator();
    if(gradeNames.hasNext()) {
      Element gradesNode = doc.createElement("grades");
      while(gradeNames.hasNext()) {
        String gradeName = (String) gradeNames.next();
        Grade grade = student.getGrade(gradeName);
        
        Element gradeNode = doc.createElement("grade");
        
        Element nameNode = doc.createElement("name");
        nameNode.appendChild(doc.createTextNode(grade.getAssignmentName()));
        gradeNode.appendChild(nameNode);
        
        Element scoreNode = doc.createElement("score");
        scoreNode.appendChild(doc.createTextNode(grade.getScore() + ""));
        gradeNode.appendChild(scoreNode);
        
        doNotes(doc, gradeNode, grade.getNotes());
        
        if(grade.getScore() == Grade.INCOMPLETE) {
          gradeNode.setAttribute("type", "INCOMPLETE");
          
        } else if(grade.getScore() == Grade.NOGRADE) {
          gradeNode.setAttribute("type", "NOGRADE");
        }
        
        List late = student.getLate();
        if(!late.isEmpty()) {
          Element lateNode = doc.createElement("late");
          
          for(int i = 0; i < late.size(); i++) {
            String name = (String) late.get(i);
            Element lateName = doc.createElement("name");
            lateName.appendChild(doc.createTextNode(name));
          }
        
          gradeNode.appendChild(lateNode);
        }

        List resubmitted = student.getResubmitted();
        if(!resubmitted.isEmpty()) {
          Element resubNode = doc.createElement("resubmitted");
          
          for(int i = 0; i < resubmitted.size(); i++) {
            String name = (String) resubmitted.get(i);
            Element resubName = doc.createElement("name");
            resubName.appendChild(doc.createTextNode(name));
          }

          gradeNode.appendChild(resubNode);
        }
        
        gradesNode.appendChild(gradeNode);
      }

      root.appendChild(gradesNode);
    }

    return(doc);
  }

}
