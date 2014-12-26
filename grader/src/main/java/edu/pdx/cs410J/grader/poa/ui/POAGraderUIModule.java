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

    bind(POASubmissionsPresenter.class).asEagerSingleton();
    bind(POASubmissionPresenter.class).asEagerSingleton();
    bind(GradeBookPresenter.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public EventBus provideEventBus() {
    return new EventBus((throwable, subscriberExceptionContext) -> {
      throwable.printStackTrace(System.err);
    });
  }

}