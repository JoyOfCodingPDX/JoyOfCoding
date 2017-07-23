package edu.pdx.cs410J.grader.scoring.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.EventBusThatPublishesUnhandledExceptionEvents;
import edu.pdx.cs410J.grader.scoring.*;

public class ProjectSubmissionScorerUIModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(ProjectSubmissionsView.class).to(ProjectSubmissionsPanel.class);
    bind(TestCasesView.class).to(TestCasesPanel.class);
    bind(TestCaseOutputView.class).to(TestCaseOutputPanel.class);
    bind(ProjectSubmissionScoreView.class).to(ProjectSubmissionScorePanel.class);

    bind(ProjectSubmissionsPresenter.class).asEagerSingleton();
    bind(TestCasesPresenter.class).asEagerSingleton();
    bind(TestCaseOutputPresenter.class).asEagerSingleton();
    bind(ProjectSubmissionScorePresenter.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public EventBus provideEventBus() {
    return new EventBusThatPublishesUnhandledExceptionEvents();
  }

}
