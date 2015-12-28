package edu.pdx.cs410J.grader;

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
    HtmlGenerator htmlGenerator = getHtmlGenerator(htmlFileName);
    generateHtmlForSurveyResponses(d2lSurveyResponses, htmlGenerator);
  }

  @VisibleForTesting
  static void generateHtmlForSurveyResponses(SurveyResponsesFromD2L d2lSurveyResponses, HtmlGenerator html) {
    String title = "Previously on CS410J...";

    html.beginTag("html");

    outputHtmlHead(html, title);

    html.beginTag("body");

    outputHtmlBodyHeader(html, title);
    outputHtmlForEachSurveyResponse(d2lSurveyResponses, html);

    html.endTag();

    html.endTag();
  }

  private static void outputHtmlForEachSurveyResponse(SurveyResponsesFromD2L allResponses, HtmlGenerator html) {
    html.beginTag("ol");

    allResponses.getQuestions().forEach((question) -> {
      html.beginTag("li");
      html.text(question);
      html.endTag();
      html.beginTag("ul");

      allResponses.getResponsesTo(question).forEach((response) -> {
        html.beginTag("li");
        html.text(response);
        html.endTag();
      });

      html.endTag();
    });

    html.endTag();
  }

  private static void outputHtmlBodyHeader(HtmlGenerator html, String title) {
    html.beginTag("h1");
    html.text(title);
    html.endTag();
    html.beginTag("p");
    html.text("Say something witty here");
    html.endTag();
  }

  private static void outputHtmlHead(HtmlGenerator html, String title) {
    html.beginTag("head");
    html.beginTag("title");
    html.text(title);
    html.endTag();
    html.endTag();
  }

  private static HtmlGenerator getHtmlGenerator(String htmlFileName) throws IOException {
    File htmlFile = new File(htmlFileName).getAbsoluteFile();
    File parent = htmlFile.getParentFile();
    if (!parent.exists()) {
      usage("Parent of HTML file " + htmlFileName + " does not exist");
    }

    Writer writer = new FileWriter(htmlFile);
    return new HtmlGenerator(writer);
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
    err.println("usage: java SurveyResponsesFromD2LGenerator d2lSurveyResponsesCsvFileName htmlFileName");
    err.println("    d2lSurveyResponsesCsvFileName     Name of the CSV survey reponses file exported from D2L");
    err.println("    htmlFileName                      Name of file to which generated HTML is written");
    err.println();
    err.println("Generates an HTML file that lists the responses to a survey from D2L");
    err.println();

    System.exit(1);
  }
}
