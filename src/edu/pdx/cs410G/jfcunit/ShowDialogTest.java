package edu.pdx.cs410G.jfcunit;

import java.awt.*;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import junit.extensions.jfcunit.*;

/**
 * This class uses the JFCUnit Swing unit testing framework to test
 * the {@link ShowTextField} class.
 */
public class ShowDialogTest extends JFCTestCase {

  JFCTestHelper helper;

  public ShowDialogTest(String name) {
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
    ShowDialog.main(new String[0]);

    // Wait for events to be processed
    awtSleep();

    Set windows = helper.getWindows();
    assertEquals(1, windows.size());
  }

  /**
   * Does the dialog box contain what we expect?
   */
  public void testDialogBox() throws Exception {
    // run the GUI
    ShowDialog.main(new String[0]);

    // Wait for events to be processed
    awtSleep();

    Window window = helper.getWindow(ShowDialog.TITLE);
    assertNotNull(window);
    
    // Get the JList and button
    JList list = 
      (JList) helper.findComponent(JList.class, window, 0);
    assertNotNull(list);
    JButton button = 
      (JButton) helper.findComponent(JButton.class, window, 0);

    String[] days = ShowDialog.DAYS;
    for (int i = 0; i < days.length; i++) {
      // Select the day of the week
      int index = i;
      int clicks = 1;
      helper.enterJListClickAndLeave(this, list, index, clicks);
      awtSleep();

      // Click the button
      MouseEventData click = new MouseEventData(this, button);
      helper.enterClickAndLeave(click);
      awtSleep();

      // Make sure the dialog popped up (remember that a JOptionPane
      // is displayed in a JDialog)
      List dialogs = helper.getShowingDialogs();
      assertEquals(1, dialogs.size());
      JDialog dialog = (JDialog) dialogs.get(0);
      assertEquals(days[i], helper.getMessageFromJDialog(dialog));
      
      helper.disposeWindow(dialog, this);
      
      awtSleep();
    }
  }

}
