package edu.pdx.cs410J.grader.scoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Author: Susham Kumar
 */

public class ProjectSubmissionOutputFileParser {

  public ProjectSubmissionOutputFileParser(Reader source) {
      if(source != null) {
          bufferedReader = new BufferedReader(source);
      }

  }

  public ProjectSubmission parse()  {

     projectSubmission = new ProjectSubmission();
     String fileContents=null;

      try {
          while((fileContents= bufferedReader.readLine()) !=null) {
            fileContents=fileContents.trim();
          if(fileContents.contains("CS410J")){              // fileContents has the student Id and Project Name.
              String[] splitContent=fileContents.split(Pattern.quote("."));
              projectSubmission.setProjectName(splitContent[splitContent.length -1]);
              projectSubmission.setStudentId(splitContent[splitContent.length -2]);

            }

            if(fileContents.contains("Submitted by")){ //fileContents has StudentName
             String [] splitContent=fileContents.split("Submitted by ");
             projectSubmission.setStudentName(splitContent[splitContent.length -1]);
            }

            if(fileContents.contains("Submitted on")){ //fileContents has Submission Time
                String [] splitContent=fileContents.split("Submitted on ");
                projectSubmission.setSubmissionTime(parseSubmissionTime(splitContent[splitContent.length -1]));

            }

            if(fileContents.contains("Graded on")){ //fileContents has Graded Time
                String [] splitContent=fileContents.split("Graded on");
                projectSubmission.setGradedTime(parseGradingTime(splitContent[splitContent.length -1].trim()));
            }
              if(fileContents.contains("out of")){ //fileContents has Total Points and Awarded Points
                  String [] splitContent=fileContents.split("out of");
                  String keyword="out of";
                  String awardedPoints=fileContents.substring(0, fileContents.indexOf(keyword));
                  if(!awardedPoints.isEmpty() && awardedPoints != "") {
                      double specifiedGrade = Double.parseDouble(awardedPoints);
                      projectSubmission.setScore(specifiedGrade);
                  }
                  projectSubmission.setTotalPoints(Double.parseDouble(splitContent[splitContent.length -1].trim()));
              }


          }

      } catch (IOException e) {
          e.printStackTrace();
      }
      catch(ParseException pe)
      {
          logger.error("While Parsing the Date"+ pe);
      }

      return  projectSubmission;
  }

  public static Date parseSubmissionTime(String submissionTimeString) throws ParseException {

    DateFormat format = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
   Date date = format.parse(submissionTimeString);

      return date;
  }

  public static Date parseGradingTime(String gradingTimeString) throws ParseException {
      DateFormat gradingTimeFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
      String  test= gradingTimeFormat.format(new Date());
      Date date = gradingTimeFormat.parse(gradingTimeString);

      return date;
  }

    private BufferedReader bufferedReader=null;
    private ProjectSubmission projectSubmission=null;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
