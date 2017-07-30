package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.Subscribe;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaded.LoadedProjectSubmission;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsLoaderSaverView.DirectorySelectedListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProjectSubmissionsLoaderSaverPresenterTest extends ProjectSubmissionTestCase {

  private final ProjectSubmissionsLoaderSaverView view = mock(ProjectSubmissionsLoaderSaverView.class);
  private final File testDirectory = new File(System.getProperty("user.dir"));

  @Override
  public void setUp() {
    super.setUp();


    try {
      ProjectSubmissionXmlConverter converter = new ProjectSubmissionXmlConverter();
      Map<File, String> submissionsXml = new LinkedHashMap<>();
      submissionsXml.put(new File(testDirectory, "one.xml"), converter.getXmlString(createProjectSubmission("Project", "one")));
      submissionsXml.put(new File(testDirectory, "two.xml"), converter.getXmlString(createProjectSubmission("Project", "two")));

      new ProjectSubmissionsLoaderSaverPresenter(bus, view, new ProjectSubmissionsLoaderSaverPresenter.ProjectSubmissionXmlLoader() {
        @Override
        public Reader getReader(File file) throws FileNotFoundException {
          return new StringReader(submissionsXml.get(file));
        }

        @Override
        public List<File> getSubmissionFiles(File directory) {
          return new ArrayList<>(submissionsXml.keySet());
        }
      });

    } catch (JAXBException e) {
      fail(e.toString());
    }
  }

  @Test
  public void submissionLoadedWhenDirectorySelected() {
    ArgumentCaptor<DirectorySelectedListener> listener = ArgumentCaptor.forClass(DirectorySelectedListener.class);
    verify(view).addDirectorySelectedListener(listener.capture());

    ProjectSubmissionsLoadedHandler handler = mock(ProjectSubmissionsLoadedHandler.class);
    bus.register(handler);

    listener.getValue().directorySelected(testDirectory);

    ArgumentCaptor<ProjectSubmissionsLoaded> event = ArgumentCaptor.forClass(ProjectSubmissionsLoaded.class);
    verify(handler).handle(event.capture());

    List<LoadedProjectSubmission> submissions = event.getValue().getLoadedSubmissions();
    assertThat(submissions.size(), equalTo(2));
    for (LoadedProjectSubmission submission : submissions) {
      assertThat(submission.getFile().getName(), equalTo(submission.getSubmission().getStudentId() + ".xml"));
    }
  }

  interface ProjectSubmissionsLoadedHandler {
    @Subscribe
    void handle(ProjectSubmissionsLoaded event);
  }
}
