package edu.pdx.cs410G.javaws;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.net.*;
import javax.jnlp.*;
import javax.swing.*;

/**
 * This class shows off the capabilities of JNLP's {@link
 * BasicService} class.
 */
public class BasicServiceExample extends JPanel {

  /**
   * Builds a GUI that displays information gathered form JNLP's
   * {@link BasicService}
   */
  public BasicServiceExample() throws UnavailableServiceException {
    this.setLayout(new BorderLayout());
    
    final BasicService service =
      (BasicService) ServiceManager.lookup("javax.jnlp.BasicService");
    JTextArea text = new JTextArea();
    this.add(BorderLayout.CENTER, text);
    text.append("Code base: " + service.getCodeBase() + "\n");
    text.append("Is offline? " + service.isOffline() + "\n");
    text.setEditable(false);

    JPanel p = new JPanel();
    final JTextField url = new JTextField(30);
    JButton browse = new JButton("Browse URL");
    browse.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  try {
	    service.showDocument(new URL(url.getText()));

	  } catch (MalformedURLException ex) {
	    return;
	  }
	}
      });
    p.add(url);
    p.add(browse);
    this.add(BorderLayout.SOUTH, p);
  }

}
