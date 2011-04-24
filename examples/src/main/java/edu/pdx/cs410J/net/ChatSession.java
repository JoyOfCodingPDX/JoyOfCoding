package edu.pdx.cs410J.net;

import java.io.*;
import java.util.*;

/**
 * This class allows two people to chat.
 */
public class ChatSession {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Main program that reads lines of text from the console, composes
   * messages from them, and receives messages from the the listener.
   * The owner, host, and port numbers for the
   * <code>ChatSession</code> are read from the command line.
   */
  public static void main(String[] args) {
    String owner = args[0];
    int port = 0;

    try {
      port = Integer.parseInt(args[1]);

    } catch (NumberFormatException ex) {
      err.println("** Bad port number: " + args[1]);
    }

    out.println("Establishing connection");

    // Make a new ChatCommunicate and start it up
    ChatCommunicator communicator = new ChatCommunicator(port);
    communicator.startup();

    // Prompt for input, read from the command line until the "bye"
    // message is inputted.
    try {
      InputStreamReader isr = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(isr);

      String line = "";
      while (!line.trim().equals("bye")) {
	// Print and read messages from the listener
	Iterator messages = communicator.getMessages().iterator();
	while(messages.hasNext()) {
	  out.println(messages.next());
	}

	// Prompt for user input
	out.print(owner + "> ");
	out.flush();

	line = br.readLine();

	if(!line.trim().equals("")) {
	  ChatMessage message = new ChatMessage(owner, line);
	  communicator.sendMessage(message);
	}
      }

      out.println("Waiting for other side to shut down");

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

}
