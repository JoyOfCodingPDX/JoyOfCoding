package edu.pdx.cs410J.net;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * A <code>ChatListener</code> runs in the background and listens for
 * messages on a socket on localhost.
 */
public class ChatListener implements Runnable {
  private static PrintStream err = System.err;

  private List incoming;  // Incoming messages
  private BufferedInputStream bis;  // Read messages from here

  /**
   * Creates a new <code>ChatListener</code>
   */
  public ChatListener() {
    this.incoming = new ArrayList();
  }

  /**
   * Sets the socket on which this <code>ChatListener</code> listens
   */
  public void setSocket(Socket socket) {
//      System.out.println("Making input stream");

    try {
      // Make streams for reading and writing
//        System.out.println("InputStream");
      this.bis = new BufferedInputStream(socket.getInputStream());

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

  /** 
   * Sit in a loop and wait for messages to come in.  Unfortunately,
   * calling the constructor of <code>ObjectInputStream</code>
   * blocks.  So, we have to encode a "STOP" command in the final
   * message.  Awful.
   */
  public void run() {
//      System.out.println("ChatListener starting");

    // Do stuff...
    while (true) {
//        System.out.println("Waiting to receive");
      
      try {
	// Is there a message to receive?
	ObjectInputStream in = new ObjectInputStream(bis);
	ChatMessage m = (ChatMessage) in.readObject();
	if(m != null) {
	  synchronized(this.incoming) {
//  	    System.out.println("Receiving: " + m);
	    this.incoming.add(m);
	  }

//  	  System.out.println("Is " + m + " last? " +
//  			     (m.isLastMessage() ? "yes" : "no"));

	  if (m.isLastMessage()) {
	    break;
	  }
	}
	  
      } catch (ClassNotFoundException ex) {
	err.println("** Could not find class: " + ex);
	System.exit(1);
	  
      } catch (IOException ex) {
	err.println("** IOException: " + ex);
	System.exit(1);
      }
    }

//      System.out.println("ChatListener stopping");
  }

  /**
   * Returns all incoming messages.  This method will be called by a
   * thread other than the one running run().
   */
  public List getMessages() {
    List messages = new ArrayList();
    synchronized(this.incoming) {
      // Why can't we just return this.incoming?

      messages.addAll(this.incoming);
      this.incoming.clear();
    }

    return messages;
  }
}
