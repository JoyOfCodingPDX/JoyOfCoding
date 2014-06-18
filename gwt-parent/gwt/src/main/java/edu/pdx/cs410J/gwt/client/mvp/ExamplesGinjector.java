package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * A gin injector that provides access to well-known GWT objects
 */
@GinModules(ExamplesGinModule.class)
public interface ExamplesGinjector extends Ginjector {

  MovieDatabaseExample getMovieDatabaseExample();

//  DivisionView getDivisionView();

  UncaughtExceptionReporter getUncaughtExceptionHandler();

  HandlerManager getEventBus();
}
