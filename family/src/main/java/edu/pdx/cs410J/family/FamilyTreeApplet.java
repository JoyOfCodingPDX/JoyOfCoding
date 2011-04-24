package edu.pdx.cs410J.family;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;

/**
 * This class is an applet that displays an immutable family tree.  
 */
@SuppressWarnings("serial")
public class FamilyTreeApplet extends JApplet {

  /**
   * Creates a <code>FamilyTreePanel</code> to view a family tree.
   */
  public void init() {
    FamilyTreePanel viewer = new FamilyTreePanel();
    viewer.addComponents();
    this.getContentPane().add(viewer);

    String xmlFile = this.getParameter("xmlFile");
    if (xmlFile != null) {
      URL url = null;
      try {
//         URL base = this.getDocumentBase();
//         url = new URL(base, xmlFile);
        url = new URL(xmlFile);
        viewer.setURLSource(url);

      } catch (MalformedURLException ex) {
        viewer.setSourceText(ex.toString());
      }
    }
  }
}
