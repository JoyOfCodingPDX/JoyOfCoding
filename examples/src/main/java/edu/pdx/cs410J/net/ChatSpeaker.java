package edu.pdx.cs410J.net;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * A <code>ChatSpeaker</code> runs in the background and sends
 * <code>ChatMessage</code>s over a <code>Socket</code>.
 */
public class ChatSpeaker implements Runnable {
  private static PrintStream err = System.err;

  private List outgoing;  // Outgoing messages
  private BufferedOutputStream bos;  // Send messages here

  /**
   * Creates a new <code>ChatSpeaker</code>
   */
  public ChatSpeaker() {
    this.outgoing = new ArrayList();
  }

  /**
   * Sets the socket to which this <code>ChatSpeaker</code> sends
   * messages.
   */
  public void setSocket(Socket socket) {
//      System.out.println("Making output stream");

    try {
      // Make streams for reading and writing
//        System.out.println("OutputStream");

      this.bos = new BufferedOutputStream(socket.getOutputStream());

    } catch (IOException ex) {
      err.println("** IOException: " + ex);
      System.exit(1);
    }
  }

  /** 
   * A <code>ChatSpeaker</code> uses wait/notify on its
   * <code>incoming</code> list to know when to send a message.  For
   * the sake of symmetry, we interrupt it to tell it to stop.
   */
  public void run() {
//      System.out.println("ChatSpeaker starting");

    while (true) {
      try {
	// Is there a message to send?
	synchronized(this.outgoing) {
	  if (!this.outgoing.isEmpty()) {
	    ChatMessage m = (ChatMessage) this.outgoing.remove(0);
	    
//  	    System.out.println("Sending: " + m);
	    
	    ObjectOutputStream out = new ObjectOutputStream(bos);
	    out.writeObject(m);
	    out.flush();

	    if (m.isLastMessage()) {
	      // Send the last message and then go home
	      break;
	    }

	  }

	  // Wait for a message
	  this.outgoing.wait();
	}

      } catch (IOException ex) {
	err.println("** IOException: " + ex);
	System.exit(1);
	break;
	
      } catch (InterruptedException ex) {
//  	System.out.println("Done sending messages");
	break;
      }
    }

//      System.out.println("ChatSpeaker stopping");
  }

  /**
   * Queues a message to be sent.  This method will be called by a
   * thread other than the one running run().
   */
  public void sendMessage(ChatMessage message) {
    synchronized(this.outgoing) {
//        System.out.println("Adding message: " + message);
      this.outgoing.add(message);
      this.outgoing.notify();
    }
  }
}
