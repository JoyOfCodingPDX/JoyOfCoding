package edu.pdx.cs410J.grader.mvp;

import com.google.common.eventbus.EventBus;

public class PresenterOnEventBus {
  private final EventBus bus;

  public PresenterOnEventBus(EventBus bus) {
    this.bus = bus;
    this.bus.register(this);
  }

  protected void publishEvent(Object event) {
    this.bus.post(event);
  }
}
