package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.io.File;

public class GradeBookPresenter {
  private final GradeBookView view;
  private final EventBus bus;

  public GradeBookPresenter(EventBus bus, GradeBookView view) {
    this.bus = bus;
    this.view = view;

    bus.register(this);

    view.addGradeBookFileListener(this::publishLoadGradeBookEvent);
  }

  private void publishLoadGradeBookEvent(File file) {
    this.bus.post(new LoadGradeBook(file));
  }

  @Subscribe
  public void displayGradeBookName(GradeBookLoaded event) {
    this.view.setGradeBookName(event.getGradeBook().getClassName());
  }
}
