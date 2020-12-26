package edu.pdx.cs410J.grader.poa;

import javax.swing.text.BadLocationException;
import java.io.IOException;

public interface POASubmissionView {

  enum POAContentType {
    TEXT("text/plain"),
    HTML("text/html");

    private final String contentType;

    POAContentType(String contentType) {
      this.contentType = contentType;
    }

    public String getContentType() {
      return this.contentType;
    }
  }

  void setSubmissionSubject(String subject);

  void setSubmissionSubmitter(String submitter);

  void setSubmissionTime(String time);

  void setContent(String content, POAContentType contentType) throws IOException, BadLocationException;
}
