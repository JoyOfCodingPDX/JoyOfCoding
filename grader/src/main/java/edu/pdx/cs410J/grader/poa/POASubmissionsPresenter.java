package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;

public class POASubmissionsPresenter {
  private final EventBus bus;
  private final POASubmissionsView view;

  public POASubmissionsPresenter(EventBus bus, POASubmissionsView view) {
    this.bus = bus;
    this.view = view;
  }
}
