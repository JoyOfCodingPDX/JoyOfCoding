package edu.pdx.cs410J.grader.poa;

import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.XmlDumper;

import java.io.File;
import java.io.IOException;

public class GradeBookTestCase extends EventBusTestCase {
  protected File writeGradeBookToFile(String gradeBookName) throws IOException {
    File file = File.createTempFile("testGradeBook", "xml");

    GradeBook book = new GradeBook(gradeBookName);
    XmlDumper dumper = new XmlDumper(file);
    dumper.dump(book);

    return file;
  }
}
