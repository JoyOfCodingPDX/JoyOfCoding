package edu.pdx.cs410J.net;

import java.io.Serializable;
import java.text.*;
import java.util.*;

/**
 * This class represents a message that is passed between two
 * <code>ChatSession</code>s.
 */
public class ChatMessage implements Serializable {

  private String sender;   // Name of the sender
  private Date   date;     // When the messsage was sent
  private String text;     // Contents of message

  /**
   * Creates a new <code>ChatMessage</code> with the current time.
   */
  public ChatMessage(String sender, String text) {
    this.sender = sender;
    this.date = new Date();
    this.text = text;
  }

  /**
   * Returns <code>true</code> if this is the last message sent
   */
  public boolean isLastMessage() {
    return this.text.trim().equals("bye");
  }

  /**
   * Returns a textual representation of this <code>ChatMessage</code>
   * that is suitable for displaying in a <code>ChatSession</code>.
   */
  public String toString() {
    DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    StringBuffer sb = new StringBuffer();
    sb.append(this.sender);
    sb.append(" [");
    sb.append(df.format(this.date));
    sb.append("]> ");
    sb.append(this.text);
    return sb.toString();
  }

}
