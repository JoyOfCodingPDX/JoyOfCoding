package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class GradeBookPresenter {
  private final GradeBookView view;

  public GradeBookPresenter(EventBus bus, GradeBookView view) {
    this.view = view;

    bus.register(this);
  }

  @Subscribe
  public void displayGradeBookName(GradeBookLoaded event) {
    this.view.setGradeBookName(event.getGradeBook().getClassName());
  }
}
