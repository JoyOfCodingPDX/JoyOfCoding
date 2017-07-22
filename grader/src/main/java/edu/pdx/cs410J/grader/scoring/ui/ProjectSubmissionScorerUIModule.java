package edu.pdx.cs410J.grader.scoring.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.mvp.EventBusThatPublishesUnhandledExceptionEvents;
import edu.pdx.cs410J.grader.scoring.ProjectSubmission;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsPresenter;
import edu.pdx.cs410J.grader.scoring.ProjectSubmissionsView;

public class ProjectSubmissionScorerUIModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(ProjectSubmissionsView.class).to(ProjectSubmissionsPanel.class);

    bind(ProjectSubmissionsPresenter.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public EventBus provideEventBus() {
    return new EventBusThatPublishesUnhandledExceptionEvents();
  }

}
