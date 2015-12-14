package edu.pdx.cs410J.gwt.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import org.junit.Test;

public class DivisionServiceExampleGwtIT extends IntegrationGwtTestCase{

  /**
   * Tests that the division service UI
   */
  @Test
  public void testDivisionUI() {
    final DivisionServiceExample ui = new DivisionServiceExample();
    int dividend = 6;
    int divisor = 3;
    final int quotient = 2;

    ui.dividend.setText(String.valueOf(dividend));
    ui.divisor.setText(String.valueOf(divisor));

    click(ui.equals);

    Timer verify = new Timer() {
      @Override
      public void run() {
        assertEquals(String.valueOf(quotient), ui.quotient.getText());
        finishTest();
      }
    };

    // Wait for the RPC call to return
    verify.schedule(500);

    delayTestFinish(1000);
  }

  /**
   * Clicks a <code>Button</code>
   *
   * One would think that you could testing clicking a button with Button.click(), but it looks
   * like you need to fire the native event instead.  Lame.
   *
   * @param button
   *        The button to click 
   */
  private void click(Button button) {
    NativeEvent event = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
    DomEvent.fireNativeEvent(event, button);
  }
}
