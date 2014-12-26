package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import org.junit.Before;

public class EventBusTestCase {
  protected EventBus bus;

  @Before
  public void setUp() {
    bus = new EventBusThatPublishesUnhandledExceptionEvents();
  }
}
