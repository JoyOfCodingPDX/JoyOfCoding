package edu.pdx.cs410J.gwt.server.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A subclass of the {@link GuiceRemoteServiceServlet} that creates the Guice injector.
 */
public class GuiceRemoteServiceServletForTesting extends GuiceRemoteServiceServlet {
  private final Injector injector = Guice.createInjector(new GwtExamplesModule());

  @Override
  protected Injector getInjector() {
    return injector;
  }
}
