package edu.pdx.cs410J.grader;

import java.io.*;
import java.util.*;

import edu.pdx.cs410J.ParserException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

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
  public XmlGradeBookParser(String fileName) throws FileNotFoundException, IOException {
    this(new File(fileName));
  }

  /**
   * Creates an <code>XmlGradeBookParser</code> that creates a
   * <code>GradeBook</code> from the contents of a <code>File</code>.
   */
  public XmlGradeBookParser(File file) throws FileNotFoundException, IOException {
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

      } else if (child.getTagName().equals("notes")) {
        Iterator notes = extractNotesFrom(child).iterator();
        while (notes.hasNext()) {
          assign.addNote((String) notes.next());
        }
      }
    }

    if (assign == null ) {
      throw new ParserException("No assignment found!");
    }
    
    String type = element.getAttribute("type");
    if (type.equals("PROJECT")) {
      assign.setType(Assignment.PROJECT);
      
    } else if (type.equals("QUIZ")) {
      assign.setType(Assignment.QUIZ);
      
    } else if (type.equals("OTHER")) {
      assign.setType(Assignment.OTHER);
    }

    return assign;
  }

  /**
   * Parses the source and from it creates a <code>GradeBook</code>.
   */
  public GradeBook parse() throws ParserException {
    // Parse the source
    Document doc = null;

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

    } catch (ParserConfigurationException ex) {
      throw new ParserException("While parsing XML source: " + ex);

    } catch (SAXException ex) {
      throw new ParserException("While parsing XML source: " + ex);

    } catch (IOException ex) {
      throw new ParserException("While parsing XML source: " + ex);
    }

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

      } else if (this.book == null) {
        throw new ParserException("name element is not first");

      } else if (child.getTagName().equals("assignments")) {
        NodeList assignments = child.getChildNodes();
        for (int j = 0; j < assignments.getLength(); j++) {
          Node assignment = assignments.item(j);
          if (assignment instanceof Element) {
            Assignment assign = 
              extractAssignmentFrom((Element) assignment);
            this.book.addAssignment(assign);
          }
        }

      } else if (child.getTagName().equals("students")) {
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

            } catch (IOException ex) {
              String s = "While parsing " + file + ": " + ex;
              throw new ParserException(s);
            }
          }         
        }
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

}
