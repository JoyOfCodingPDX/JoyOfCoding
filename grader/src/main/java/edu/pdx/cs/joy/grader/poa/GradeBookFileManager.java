package edu.pdx.cs.joy.grader.poa;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.pdx.cs.joy.ParserException;
import edu.pdx.cs.joy.grader.gradebook.GradeBook;
import edu.pdx.cs.joy.grader.gradebook.XmlDumper;
import edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser;

import java.io.File;
import java.io.IOException;

public class GradeBookFileManager {
  private final EventBus bus;
  private File file;

  @Inject
  public GradeBookFileManager(EventBus bus) {
    this.bus = bus;
    this.bus.register(this);
  }

  @Subscribe
  public void loadGradeBookFromFile(LoadGradeBook event) throws IOException, ParserException {
    file = event.getFile();
    XmlGradeBookParser parser = new XmlGradeBookParser(file);
    GradeBook book = parser.parse();

    this.bus.post(new GradeBookLoaded(book));
  }

  @Subscribe
  public void writeGradeBookToFile(SaveGradeBook event) throws IOException {
    XmlDumper dumper = new XmlDumper(this.file);
    dumper.dump(event.getGradeBook());
  }
}
