package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import edu.pdx.cs410J.grader.poa.*;

public class POAGraderUIModule extends AbstractModule {
  @Override
  protected void configure() {

    bind(POASubmissionsView.class).to(POASubmissionsPanel.class);
    bind(POASubmissionView.class).to(POASubmissionInformationWidgets.class);
    bind(GradeBookView.class).to(GradeBookWidget.class);
    bind(UnhandledExceptionView.class).to(UnhandledExceptionDialog.class).asEagerSingleton();
    bind(StudentsView.class).to(StudentsWidget.class);
    bind(POAAssignmentsView.class).to(POAAssignmentsWidget.class);
    bind(POAGradeView.class).to(POAGradeWidgets.class);
    bind(EmailCredentialsView.class).to(EmailCredentialsDialog.class).asEagerSingleton();

    bind(POASubmissionsPresenter.class).asEagerSingleton();
    bind(POASubmissionPresenter.class).asEagerSingleton();
    bind(GradeBookPresenter.class).asEagerSingleton();
    bind(UnhandledExceptionPresenter.class).asEagerSingleton();
    bind(StudentsPresenter.class).asEagerSingleton();
    bind(POAAssignmentsPresenter.class).asEagerSingleton();
    bind(POAGradePresenter.class).asEagerSingleton();
    bind(EmailCredentialsPresenter.class).asEagerSingleton();

    bind(GradeBookFileManager.class).asEagerSingleton();
    bind(POASubmissionsDownloader.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public EventBus provideEventBus() {
    return new EventBusThatPublishesUnhandledExceptionEvents();
  }

}
