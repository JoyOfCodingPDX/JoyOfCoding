package edu.pdx.cs410J.grader;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.io.InputStream;

public interface EmailAttachmentProcessor {
  void processAttachment(Message message, String fileName, InputStream inputStream, String contentType);

  Iterable<? extends String> getSupportedContentTypes();

  default boolean hasSupportedContentType(Message message) throws MessagingException {
    for (String supported : getSupportedContentTypes()) {
      if (message.isMimeType(supported)) {
        return true;
      }
    }

    return false;
  }
}
