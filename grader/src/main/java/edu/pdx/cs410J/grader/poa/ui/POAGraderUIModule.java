package edu.pdx.cs410J.grader.poa.ui;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import edu.pdx.cs410J.grader.poa.POASubmissionsPresenter;
import edu.pdx.cs410J.grader.poa.POASubmissionsView;

public class POAGraderUIModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(EventBus.class).toInstance(new EventBus("PlanOfAttackGrader"));

    bind(POASubmissionsView.class).to(POASubmissionsPanel.class);

    bind(POASubmissionsPresenter.class).asEagerSingleton();
  }
}
