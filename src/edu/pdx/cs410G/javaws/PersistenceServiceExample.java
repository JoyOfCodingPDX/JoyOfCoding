package edu.pdx.cs410G.javaws;

import edu.pdx.cs410G.swing.TimerExample;
import java.awt.BorderLayout;
import java.io.*;
import java.net.URL;
import java.util.Date;
import javax.jnlp.*;
import javax.swing.*;

/**
 * This class demonstrates the {@link PersistenceService} class that
 * allows JNLP applications to write pieces of data on the local
 * machine.  The data is keyed on a {@link URL} and is represented
 * as a {@link FileContents}.
 */
public class PersistenceServiceExample extends JPanel {

  /**
   * Creates a new <code>PersistentServiceExample</code> that displays
   * the current time along with the last time this application was
   * accessed.  The last time accessed is stored as a serialized
   * {@link Date} on the local machine.
   */
  public PersistenceServiceExample() 
    throws UnavailableServiceException {

    this.setLayout(new BorderLayout());
    
    Date startTime = new Date();
    this.add(new TimerExample(), BorderLayout.CENTER);

    // Use the JNLP API to get the last time this application was
    // started
    String serviceName = "javax.jnlp.PersistenceService";
    PersistenceService ps = 
      (PersistenceService) ServiceManager.lookup(serviceName);
    serviceName = "javax.jnlp.BasicService";
    BasicService bs = 
      (BasicService) ServiceManager.lookup(serviceName); 
    Date lastStartTime = null;
    try {
      URL codebase = bs.getCodeBase();
      URL key = new URL(codebase, "JNLPExample/lastStartTime");
      try {
	FileContents file = ps.get(key);
	ObjectInputStream ois =
	  new ObjectInputStream(file.getInputStream());
	lastStartTime = (Date) ois.readObject();

      } catch (FileNotFoundException ex) {
	// Couldn't find file, create a new one
	int size = 1000;
	ps.create(key, size);
      }

      // Write the current start time to the file
      FileContents file = ps.get(key);
      ObjectOutputStream oos =
	new ObjectOutputStream(file.getOutputStream(true));
      oos.writeObject(startTime);
      oos.flush();
      oos.close();

    } catch (IOException ex) {
      ex.printStackTrace(System.err);

    } catch (ClassNotFoundException ex) {
      ex.printStackTrace(System.err);
    }

    JLabel label = new JLabel();
    this.add(label, BorderLayout.SOUTH);
    if (lastStartTime != null) {
      label.setText("Last accessed on " + lastStartTime);

    } else {
      label.setText("Never accessed before");
    } 

  }

}
