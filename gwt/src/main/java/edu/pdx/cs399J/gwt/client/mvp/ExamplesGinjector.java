package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * A gin injector that provides access to well-known GWT objects
 */
@GinModules(ExamplesGinModule.class)
public interface ExamplesGinjector extends Ginjector {

  MovieDatabaseExample getMovieDatabaseExample();

//  DivisionView getDivisionView();

  UncaughtExceptionHandlerReporter getUncaughtExceptionHandler();
}
