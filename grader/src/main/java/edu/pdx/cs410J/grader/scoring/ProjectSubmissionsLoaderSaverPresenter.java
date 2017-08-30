package edu.pdx.cs410J.grader.scoring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaded.LoadedProjectSubmission;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Singleton
public class ProjectSubmissionsLoaderSaverPresenter extends PresenterOnEventBus {
  public static final String CLICK_HERE_MESSAGE = "Click above to load submission files";
  private final ProjectSubmissionsLoaderSaverView view;
  private final ProjectSubmissionXmlConverter converter;
  private final ProjectSubmissionXmlLoader xmlLoader;
  private final ProjectSubmissionXmlWriter xmlWriter;
  private List<LoadedProjectSubmission> loadedProjectSubmissions;

  @Inject
  public ProjectSubmissionsLoaderSaverPresenter(EventBus bus, ProjectSubmissionsLoaderSaverView view) throws JAXBException {
    this(bus, view, new ProjectSubmissionXmlLoader() {
      @Override
      public Reader getReader(File file) throws FileNotFoundException {
        return new FileReader(file);
      }

      @Override
      public List<File> getSubmissionFiles(File directory) {
        File[] xmlFiles = directory.listFiles((dir, name) -> name.endsWith(".xml"));
        return Arrays.asList(xmlFiles);
      }
    }, new ProjectSubmissionXmlWriter() {
      @Override
      public void writeXmlToFile(File file, String xml) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
          writer.write(xml);
          writer.flush();
        }
      }
    });
  }

  @VisibleForTesting
  ProjectSubmissionsLoaderSaverPresenter(EventBus bus, ProjectSubmissionsLoaderSaverView view,
                                         ProjectSubmissionXmlLoader xmlLoader,
                                         ProjectSubmissionXmlWriter xmlWriter) throws JAXBException {
    super(bus);
    this.view = view;
    this.xmlLoader = xmlLoader;
    this.xmlWriter = xmlWriter;

    this.converter = new ProjectSubmissionXmlConverter();

    this.view.addDirectorySelectedListener(this::loadSubmissionsFromDirectory);
    this.view.addSaveSubmissionsListener(this::saveSubmissionsToXmlFiles);
    this.view.setDisplayMessage(CLICK_HERE_MESSAGE);
  }

  private void saveSubmissionsToXmlFiles() {
    loadedProjectSubmissions.forEach(this::saveSubmissionToXmlFile);
  }

  private void saveSubmissionToXmlFile(LoadedProjectSubmission submission) {
    try {
      String xml = this.converter.getXmlString(submission.getSubmission());
      this.xmlWriter.writeXmlToFile(submission.getFile(), xml);

    } catch (JAXBException | IOException e) {
      throw new IllegalStateException("While saving XML", e);
    }

  }

  private void loadSubmissionsFromDirectory(File directory) {
    loadedProjectSubmissions = new ArrayList<>();
    List<File> xmlFiles = getSubmissionFiles(directory);
    for (File xmlFile : xmlFiles) {
      ProjectSubmission submission = null;
      try {
        submission = this.converter.convertFromXml(getReader(xmlFile));
      } catch (JAXBException | FileNotFoundException e) {
        throw new IllegalStateException("While parsing XML", e);
      }
      loadedProjectSubmissions.add(new LoadedProjectSubmission(xmlFile, submission));
    }

    this.view.setDisplayMessage(loadedSubmissionsFrom(directory));
    publishEvent(new ProjectSubmissionsLoaded(loadedProjectSubmissions));
  }

  private Reader getReader(File file) throws FileNotFoundException {
    return this.xmlLoader.getReader(file);
  }

  private List<File> getSubmissionFiles(File directory) {
    return this.xmlLoader.getSubmissionFiles(directory);
  }

  public static String loadedSubmissionsFrom(File testDirectory) {
    return testDirectory.getAbsolutePath();
  }

  @VisibleForTesting
  interface ProjectSubmissionXmlLoader {
    Reader getReader(File file) throws FileNotFoundException;

    List<File> getSubmissionFiles(File directory);
  }

  @VisibleForTesting
  interface ProjectSubmissionXmlWriter {
    void writeXmlToFile(File file, String xml) throws IOException;
  }
}
