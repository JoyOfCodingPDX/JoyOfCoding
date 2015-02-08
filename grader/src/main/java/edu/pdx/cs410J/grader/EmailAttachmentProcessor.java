package edu.pdx.cs410J.grader;

import javax.mail.Message;
import java.io.InputStream;

public interface EmailAttachmentProcessor {
  void processAttachment(Message message, String fileName, InputStream inputStream);

  Iterable<? extends String> getSupportedContentTypes();

}
