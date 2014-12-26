package edu.pdx.cs410J.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.grader.GradeBook;
import edu.pdx.cs410J.grader.XmlGradeBookParser;

import java.io.File;
import java.io.IOException;

public class GradeBookLoader {
  private final EventBus bus;

  @Inject
  public GradeBookLoader(EventBus bus) {
    this.bus = bus;
    this.bus.register(this);
  }

  @Subscribe
  public void loadGradeBookFromFile(LoadGradeBook event) throws IOException, ParserException {
    File file = event.getFile();
    XmlGradeBookParser parser = new XmlGradeBookParser(file);
    GradeBook book = parser.parse();

    this.bus.post(new GradeBookLoaded(book));
  }
}
