package edu.pdx.cs410J.grader;

import edu.pdx.cs410J.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class SurveyResponsesFromD2LGenerator {

  private static final Logger logger = LoggerFactory.getLogger("edu.pdx.cs410J.grader");

  public static void main(String[] args) throws IOException, ParserException {
    String d2lCsvFileName = null;
    String htmlFileName = null;

    for (String arg : args) {
      if (d2lCsvFileName == null) {
        d2lCsvFileName = arg;

      } else if (htmlFileName == null) {
        htmlFileName = arg;

      } else {
        usage("Extraneous command line argument: " + arg);
      }
    }

    if (d2lCsvFileName == null) {
      usage("Missing D2L CSV file name");
    }

    if (htmlFileName == null) {
      usage("Missing html file name");
    }

    SurveyResponsesFromD2L d2lSurveyResponses = parseD2lCsvFile(d2lCsvFileName);
    File htmlFile = getHtmlFile(htmlFileName);
    generateHtmlForSurveyResponses(d2lSurveyResponses, htmlFile);

  }

  private static void generateHtmlForSurveyResponses(SurveyResponsesFromD2L d2lSurveyResponses, File htmlFile) {


  }

  private static File getHtmlFile(String htmlFileName) {
    File htmlFile = new File(htmlFileName);
    File parent = htmlFile.getParentFile();
    if (!parent.exists()) {
      usage("Parent of HTML file " + htmlFileName + " does not exist");
    }

    return htmlFile;
  }


  private static SurveyResponsesFromD2L parseD2lCsvFile(String d2lCsvFileName) throws IOException {
    File d2lCsvFile = new File(d2lCsvFileName);
    if (!d2lCsvFile.exists()) {
      usage("D2L CSV file \"" + d2lCsvFile + "\" does not exist");
    }

    D2LSurveyResponsesCSVParser parser = new D2LSurveyResponsesCSVParser(new FileReader(d2lCsvFile));
    return parser.getSurveyResponses();
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java GradesFromD2LImporter d2lGradesCsvFileName gradeBookFileName");
    err.println("    d2lGradesCsvFileName       Name of the CSV grades file exported from D2L");
    err.println("    gradeBookFileName    Gradebook file");
    err.println();
    err.println("Imports grades from D2L into a gradebook");
    err.println();

    System.exit(1);
  }
}
