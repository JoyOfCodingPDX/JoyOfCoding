package edu.pdx.cs410J.grader;

import java.io.InputStream;

public interface EmailAttachmentProcessor {
  void processAttachment(String fileName, InputStream inputStream);
}
