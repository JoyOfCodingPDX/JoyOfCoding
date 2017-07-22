package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class TestCasesPresenter extends PresenterOnEventBus {
  private final TestCasesView view;
  private List<TestCaseOutput> testCaseOutputs;

  @Inject
  public TestCasesPresenter(EventBus bus, TestCasesView view) {
    super(bus);
    this.view = view;
  }

  @Subscribe
  public void populateViewWhenSubmissionIsSelected(ProjectSubmissionSelected selected) {
    testCaseOutputs = selected.getProjectSubmission().getTestCaseOutputs();
    List<String> testCaseNames = testCaseOutputs.stream().map(TestCaseOutput::getName).collect(Collectors.toList());
    view.setTestCaseNames(testCaseNames);

    view.setSelectedTestCaseName(0);
  }
}
