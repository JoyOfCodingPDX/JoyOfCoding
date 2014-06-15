package edu.pdx.cs410J.gwt.client;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Demonstrates GWT's {@link Timer} and date formatting capabilities
 */
public class TimerExample extends Example {
  public TimerExample() {
    super("Timer Example");

    final Label time = new Label();
    Timer timer = new Timer() {

      public void run() {
        Date now = new Date();
        DateTimeFormat format = DateTimeFormat.getFullDateTimeFormat();
        time.setText("The time is" + format.format(now));
      }
    };
    timer.scheduleRepeating(1000);

    add(time);
  }
}
