package edu.pdx.cs410G.jfcunit;

import java.awt.*;
import java.util.Set;
import javax.swing.*;
import junit.extensions.jfcunit.*;

/**
 * This class uses the JFCUnit Swing unit testing framework to test
 * the {@link ShowTextField} class.
 */
public class ShowTextFieldTest extends JFCTestCase {

  JFCTestHelper helper;

  public ShowTextFieldTest(String name) {
    super(name);
  }

  public void setUp() {
    helper = new JFCTestHelper();
  }

  public void tearDown() {
    helper.cleanUp(this);
  }

  ////////  Test methods

  /**
   * Does the frame pop up?
   */
  public void testFrame() {
    // run the GUI
    ShowTextField.main(new String[0]);

    // Wait for events to be processed
    awtSleep();

    Set windows = helper.getWindows();
    assertEquals(1, windows.size());
  }

  /**
   * Does the text field contain the right text?
   */
  public void testTextFieldContents() throws Exception {
    // run the GUI
    ShowTextField.main(new String[0]);

    // Wait for events to be processed
    awtSleep();

    Window window = helper.getWindow(ShowTextField.TITLE);
    assertNotNull(window);
    
    // Get the text field
    JTextField text = 
      (JTextField) helper.findComponent(JTextField.class, window, 0);
    assertNotNull(text);
    assertEquals(ShowTextField.TEXT, text.getText());
  }

}
