package edu.pdx.cs410J.net;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * A <code>ChatCommunicator</code> obtains a <code>Socket</code> and
 * then creates a <code>ChatSpeaker</code> and a
 * <code>ChatListener</code> that run in their own threads.
 */
public class ChatCommunicator implements Runnable {
  private static PrintStream err = System.err;

  private int port;       // Where the socket is
  private ChatSpeaker speaker;    // Send messsages
  private ChatListener listener;  // Receives messages

  /**
   * Creates a new <code>ChatCommunicator</code> on a given port.
   *
   */
  public ChatCommunicator(int port) {
    this.port = port;
  }

  /**
   * Starts up this <code>ChatCommunicator</code>.
   */
  public void startup() {
    this.speaker = new ChatSpeaker();
    this.listener = new ChatListener();
    (new Thread(this)).start();
  }

  /** 
   * Make the connection to the socket.  If it cannot open a
   * <code>Socket</code>, is starts a <code>SocketServer</code> and
   * waits for a connection.  Then, start the speaker and listener.
   */
  public void run() {
    // Attempt to make a socket
    Socket socket = null;
    try {
      socket = new Socket("localhost", port);

    } catch (IOException ex) {
      // Nobody listening
//        System.out.println("Nobody's listening");
    }

    if (socket == null) {
      // Listen
      try {
	ServerSocket server = new ServerSocket(port, 10);
	socket = server.accept();

      } catch (IOException ex) {
	err.println("** IOException: " + ex);
	System.exit(1);
      }
    }

    this.speaker.setSocket(socket);
    this.listener.setSocket(socket);

    (new Thread(this.speaker)).start();
    (new Thread(this.listener)).start();
  }

  /**
   * Delegates to the <code>ChatSpeaker</code>
   */
  public void sendMessage(ChatMessage message) {
    this.speaker.sendMessage(message);
  }

  /**
   * Gets messages from the <code>ChatListener</code>
   */
  public List getMessages() {
    return this.listener.getMessages();
  }
}
