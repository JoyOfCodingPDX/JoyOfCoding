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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Susham Kumar
 */

public class ProjectSubmissionOutputFileParser {

    private final BufferedReader bufferedReader;
    private static final String SUBMITTEDBY="Submitted by";
    private static final String SUBMITTEDON="Submitted on";
    private static final String GRADEDON="Graded on";


  public ProjectSubmissionOutputFileParser(Reader source) {
      if(source != null) {
          bufferedReader = new BufferedReader(source);
      }
      else
          throw new NullPointerException();

  }

  public ProjectSubmission parse() throws ParseException, IOException, InvalidFileContentException {

     projectSubmission = new ProjectSubmission();
     String fileContents=null;


          while((fileContents= bufferedReader.readLine()) !=null) {
            fileContents=fileContents.trim();
          if(fileContents.contains("CS410J")){              // fileContents has the student Id and Project Name.
              String[] splitContent=fileContents.split(Pattern.quote("."));
              projectSubmission.setProjectName(splitContent[splitContent.length -1]);
              projectSubmission.setStudentId(splitContent[splitContent.length -2]);

            }

            if(fileContents.contains(SUBMITTEDBY)){ //fileContents has StudentName
             String [] splitContent=fileContents.split(SUBMITTEDBY);
             projectSubmission.setStudentName(splitContent[splitContent.length -1].trim());
            }

            if(fileContents.contains(SUBMITTEDON)){ //fileContents has Submission Time
                String [] splitContent=fileContents.split(SUBMITTEDON);
                projectSubmission.setSubmissionTime(parseSubmissionTime(splitContent[splitContent.length -1].trim()));

            }

            if(fileContents.contains(GRADEDON)){ //fileContents has Graded Time
                String [] splitContent=fileContents.split(GRADEDON);
                projectSubmission.setGradedTime(parseGradingTime(splitContent[splitContent.length -1].trim()));
            }

            if(fileContents.contains("out of")){ //fileContents has Total Points and Awarded Points

                  Pattern rightpattern = Pattern.compile("(?<=out of).*");
                  Pattern leftpattern=Pattern.compile(".*(?=out of)");
                  Matcher rightmatcher = rightpattern.matcher(fileContents);
                  Matcher leftmatcher = leftpattern.matcher(fileContents);
                  if (rightmatcher.find()) {
                      projectSubmission.setTotalPoints(Double.parseDouble(rightmatcher.group().trim()));
                  }
                  else
                  {
                      throw new InvalidFileContentException("Not able to retrieve Total Points");
                  }
                  if(leftmatcher.find()){

                      projectSubmission.setScore(Double.parseDouble(leftmatcher.group().trim()));
                  }
                  else {
                      throw new InvalidFileContentException("Not able to retrieve the Score");
                  }
              }
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


    private ProjectSubmission projectSubmission;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
}
