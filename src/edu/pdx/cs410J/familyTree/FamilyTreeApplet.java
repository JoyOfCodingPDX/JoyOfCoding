package edu.pdx.cs410J.familyTree;

import java.awt.*;
import java.awt.event.*;

import java.net.*;

import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import edu.pdx.cs410J.ParserException;

/**
 * This class is an applet that displays an immutable family tree.  
 */
public class FamilyTreeApplet extends JApplet {

  /**
   * Creates a <code>FamilyTreePanel</code> to view a family tree.
   */
  public void init() {
    FamilyTreePanel viewer = new FamilyTreePanel();
    viewer.addComponents();
    this.getContentPane().add(viewer);

    String xmlFile = this.getParameter("xmlFile");
    if(xmlFile != null) {
      URL url = null;
      try {
//         URL base = this.getDocumentBase();
//         url = new URL(base, xmlFile);
        url = new URL(xmlFile);
        viewer.setURLSource(url);

      } catch(MalformedURLException ex) {
        viewer.setSourceText(ex.toString());
      }
    }
  }
}
