package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;

import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * This class creates a <code>GradeBook</code> from the contents of an
 * XML file.
 */
public class XmlParser {
  private GradeBook book;    // The grade book we're creating
  private InputStream in;    // Input source of grade book
  private File studentDir;   // Where the student XML file live

  /**
   * Creates an <code>XmlParser</code> that creates a
   * <code>GradeBook</code> from a file of a given name.
   */
  public XmlParser(String fileName) throws FileNotFoundException, IOException {
    this(new File(fileName));
  }

  /**
   * Creates an <code>XmlParser</code> that creates a
   * <code>GradeBook</code> from the contents of a <code>File</code>.
   */
  public XmlParser(File file) throws FileNotFoundException, IOException {
    this(new FileInputStream(file));
    this.setStudentDir(file.getCanonicalFile().getParentFile());
  }

  /**
   * Creates an <code>XmlParser</code> that creates a
   * <code>GradeBook</code> from the contents of an
   * <code>InputStream</code>.
   */
  XmlParser(InputStream in) {
    this.in = in;
  }

  /**
   * Sets the directory in which the XML files for students are
   * generated.
   */
  public void setStudentDir(File dir) {
    if(!dir.exists()) {
      throw new IllegalArgumentException(dir + " does not exist");
    }

    if(!dir.isDirectory()) {
      throw new IllegalArgumentException(dir + " is not a directory");
    }

    this.studentDir = dir;
  }

  /**
   * Extracts the text from an <code>Element</code>.
   */
  private static String extractTextFrom(Element element) {
    Text text = (Text) element.getFirstChild();
    return((text == null ? "" : text.getData()));
  }

  /**
   * Extracts a bunch of notes from an <code>Element</code>
   */
  private static List extractNotesFrom(Element element) {
    List list = new ArrayList();

    NodeList children = element.getChildNodes();
    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if(!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if(child.getTagName().equals("note")) {
        list.add(extractTextFrom(child));
      }
    }

    return(list);
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
    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if(!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if(child.getTagName().equals("name")) {
        name = extractTextFrom(child);

      } else if(child.getTagName().equals("description")) {
        description = extractTextFrom(child);

      } else if(child.getTagName().equals("points")) {
        String points = extractTextFrom(child);
        try {
          if(name == null) {
            throw new ParserException("No name for assignment with " +
                                      "points " + points);
          }
          assign = new Assignment(name, Double.parseDouble(points));
          if(description != null) {
            assign.setDescription(description);
          }

        } catch(NumberFormatException ex) {
          throw new ParserException("Invalid points value: " +
                                    points);
        }

      } else if(child.getTagName().equals("notes")) {
        Iterator notes = extractNotesFrom(child).iterator();
        while(notes.hasNext()) {
          assign.addNote((String) notes.next());
        }
      }
    }

    if(assign == null ) {
      throw new ParserException("No assignment found!");
    }
    
    String type = element.getAttribute("type");
    if(type.equals("PROJECT")) {
      assign.setType(Assignment.PROJECT);
      
    } else if(type.equals("QUIZ")) {
      assign.setType(Assignment.QUIZ);
      
    } else if(type.equals("OTHER")) {
      assign.setType(Assignment.OTHER);
    }

    return(assign);
  }

  /**
   * Extracts a <code>Grade</code> from a chuck of a DOM tree
   */ 
  private Grade extractGradeFrom(Element root) throws ParserException {
    String name = null;
    String score = null;
    List notes = null;

    NodeList kids = root.getChildNodes();
    for(int j = 0; j < kids.getLength(); j++) {
      Node kidNode = kids.item(j);
      if(!(kidNode instanceof Element)) {
        continue;
      }
      
      Element kid = (Element) kidNode;
      if(kid.getTagName().equals("name")) {
        name = extractTextFrom(kid);
        
      } else if(kid.getTagName().equals("score")) {
        score = extractTextFrom(kid);
        
      } else if(kid.getTagName().equals("notes")) {
        notes = extractNotesFrom(kid);
      }
    }
    
    if(name == null || score == null) {
      throw new ParserException("Malformed grade");
    }
    
    try {
      double s = Double.parseDouble(score);
      Grade grade = new Grade(name, s);
      Iterator iter = notes.iterator();
      while(iter.hasNext()) {
        String note = (String) iter.next();
        grade.addNote(note);
      }

      return(grade);

    } catch(NumberFormatException ex) {
      throw new ParserException("Malformatted number: " + score);
    }
  }

  /**
   * Searches for the XML file that represents the student, parses it,
   * and fills in the <code>Student</code> accordingly.
   */
  private void fillInStudent(Student student) 
    throws ParserException {

    // Locate the XML file for the Student
    File file = new File(this.studentDir, student.getId() + ".xml");
    if(!file.exists()) {
      throw new IllegalArgumentException("No XML file for " +
                                         student.getId());
    }

    // Parse the XML file
    DOMParser parser = null;
    try {
      InputSource source = new InputSource(new FileReader(file));
      parser = new DOMParser();
      try {
        String f = "http://apache.org/xml/features/dom/defer-node-expansion";
        parser.setFeature(f, false);
        parser.setFeature("http://xml.org/sax/features/validation", true);
        
        // Since we traverse all the nodes, its more efficient to not
        // use deferred nodes, but the exceptions are ignorable if
        // there's a glitch.
        
      } catch (SAXNotRecognizedException snr) {
      } catch (SAXNotSupportedException sns) {
      }
      
      parser.parse(source);

    } catch(SAXException ex) {
      throw new ParserException("While parsing XML source: " + ex);

    } catch(IOException ex) {
      throw new ParserException("While parsing XML source: " + ex);
    }

    Document doc = parser.getDocument();
    Element root = null;
    if (doc != null) {
      root = doc.getDocumentElement();
    }
    if (doc == null || root == null) {
      throw new ParserException("Document parsing failed");
    }

    if(!root.getTagName().equals("student")) {
      throw new ParserException(file + 
                                " does not contain a student");
    }

    NodeList children = root.getChildNodes();
    for(int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
      if(!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if(child.getTagName().equals("id")) {
        String id = extractTextFrom(child);
        if(!id.equals(student.getId())) {
          throw new ParserException(file + 
                                    " does not contain student " +
                                    student.getId());
        }

      } else if(child.getTagName().equals("firstName")) {
        String firstName = extractTextFrom(child);
        student.setFirstName(firstName);

      } else if(child.getTagName().equals("lastName")) {
        String lastName = extractTextFrom(child);
        student.setLastName(lastName);

      } else if(child.getTagName().equals("nickName")) {
        String nickName = extractTextFrom(child);
        student.setNickName(nickName);

      } else if(child.getTagName().equals("email")) {
        String email = extractTextFrom(child);
        student.setEmail(email);

      } else if(child.getTagName().equals("major")) {
        String major = extractTextFrom(child);
        student.setMajor(major);

      } else if(child.getTagName().equals("grades")) {
        NodeList kids = child.getChildNodes();
        for(int j = 0; j < kids.getLength(); j++) {
          Node kidNode = kids.item(j);
          if(!(kidNode instanceof Element)) {
            continue;
          }

          Element kid = (Element) kidNode;
          if(kid.getTagName().equals("grade")) {
            Grade grade = extractGradeFrom(kid);
            student.setGrade(grade.getAssignmentName(), grade);
          }
        }

      } else if(child.getTagName().equals("late")) {
        NodeList kids = child.getChildNodes();
        for(int j = 0; j < kids.getLength(); j++) {
          Node kidNode = kids.item(j);
          if(!(kidNode instanceof Element)) {
            continue;
          }

          Element kid = (Element) kidNode;
          if(kid.getTagName().equals("name")) {
            student.addLate(extractTextFrom(kid));
          }
        }

      } else if(child.getTagName().equals("resubmitted")) {
        NodeList kids = child.getChildNodes();
        for(int j = 0; j < kids.getLength(); j++) {
          Node kidNode = kids.item(j);
          if(!(kidNode instanceof Element)) {
            continue;
          }

          Element kid = (Element) kidNode;
          if(kid.getTagName().equals("name")) {
            student.addResubmitted(extractTextFrom(kid));
          }
        }

      } else if(child.getTagName().equals("notes")) {
        Iterator notes = extractNotesFrom(child).iterator();
        while(notes.hasNext()) {
          String note = (String) notes.next();
          student.addNote(note);
        }
      }
      
    }
  }

  /**
   * Parses the source and from it creates a <code>GradeBook</code>.
   */
  public GradeBook parse() throws ParserException {
    // Parse the source
    DOMParser parser = new DOMParser();
    try {
      String f = "http://apache.org/xml/features/dom/defer-node-expansion";
      parser.setFeature(f, false);
      parser.setFeature("http://xml.org/sax/features/validation", true);

      // Since we traverse all the nodes, its more efficient to not
      // use deferred nodes, but the exceptions are ignorable if
      // there's a glitch.

    } catch (SAXNotRecognizedException snr) {
    } catch (SAXNotSupportedException sns) {
    }

    try {
      parser.parse(new InputSource(in));

    } catch(SAXException ex) {
      throw new ParserException("While parsing XML source: " + ex);

    } catch(IOException ex) {
      throw new ParserException("While parsing XML source: " + ex);
    }

    Document doc = parser.getDocument();
    Element root = null;
    if (doc != null) {
      root = doc.getDocumentElement();
    }
    if (doc == null || root == null) {
      throw new ParserException("Document parsing failed");
    }

    NodeList children = root.getChildNodes();
    for(int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      if(!(node instanceof Element)) {
        continue;
      }

      Element child = (Element) node;
      if(child.getTagName().equals("name")) {
        this.book = new GradeBook(extractTextFrom(child));

      } else if(this.book == null) {
        throw new ParserException("name element is not first");

      } else if(child.getTagName().equals("assignments")) {
        NodeList assignments = child.getChildNodes();
        for(int j = 0; j < assignments.getLength(); j++) {
          Node assignment = assignments.item(j);
          if(assignment instanceof Element) {
            Assignment assign = 
              extractAssignmentFrom((Element) assignment);
            this.book.addAssignment(assign);
          }
        }

      } else if(child.getTagName().equals("students")) {
        NodeList students = child.getChildNodes();
        for(int j = 0; j < students.getLength(); j++) {
          Node student = students.item(j);

          if(!(student instanceof Element)) {
            continue;
          }
              
          Element id = (Element) student;
          if(id.getTagName().equals("id")) {
            Student stu = new Student(extractTextFrom(id));
            fillInStudent(stu);
            this.book.addStudent(stu);
          }         
        }
      } else if(child.getTagName().equals("lateDays")) {
        // Fill in later, maybe.
      }
    }

    return(this.book);
  }

}
