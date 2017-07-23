package edu.pdx.cs410J.grader.scoring;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.PresenterOnEventBus;

@Singleton
public class TestCaseOutputPresenter extends PresenterOnEventBus {
  private final TestCaseOutputView view;

  @Inject
  public TestCaseOutputPresenter(EventBus bus, TestCaseOutputView view) {
    super(bus);
    this.view = view;
  }

  @Subscribe
  public void populateViewWhenTestCaseSelected(TestCaseSelected selected) {
    TestCaseOutput testCase = selected.getTestCaseOutput();
    view.setCommand(testCase.getCommand());
    view.setDescription(testCase.getDescription());
    view.setOutput(testCase.getOutput());
  }
}
