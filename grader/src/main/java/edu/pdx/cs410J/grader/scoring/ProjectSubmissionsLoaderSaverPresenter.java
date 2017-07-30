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
  private final ProjectSubmissionsLoaderSaverView view;
  private final ProjectSubmissionXmlConverter converter;
  private final ProjectSubmissionXmlLoader xmlLoader;

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
    });
  }

  @VisibleForTesting
  ProjectSubmissionsLoaderSaverPresenter(EventBus bus, ProjectSubmissionsLoaderSaverView view, ProjectSubmissionXmlLoader xmlLoader) throws JAXBException {
    super(bus);
    this.view = view;
    this.xmlLoader = xmlLoader;

    this.view.addDirectorySelectedListener(this::loadSubmissionsFromDirectory);
    this.converter = new ProjectSubmissionXmlConverter();
  }

  private void loadSubmissionsFromDirectory(File directory) {
    List<LoadedProjectSubmission> loaded = new ArrayList<>();
    List<File> xmlFiles = getSubmissionFiles(directory);
    for (File xmlFile : xmlFiles) {
      ProjectSubmission submission = null;
      try {
        submission = this.converter.convertFromXml(getReader(xmlFile));
      } catch (JAXBException | FileNotFoundException e) {
        Object result;
        throw new IllegalStateException("While parsing XML", e);
      }
      loaded.add(new LoadedProjectSubmission(xmlFile, submission));
    }

    publishEvent(new ProjectSubmissionsLoaded(loaded));
  }

  private Reader getReader(File file) throws FileNotFoundException {
    return this.xmlLoader.getReader(file);
  }

  private List<File> getSubmissionFiles(File directory) {
    return this.xmlLoader.getSubmissionFiles(directory);
  }

  @VisibleForTesting
  static interface ProjectSubmissionXmlLoader {
    Reader getReader(File file) throws FileNotFoundException;

    List<File> getSubmissionFiles(File directory);
  }
}
