package edu.pdx.cs399J.gwt.server.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * A subclass of the {@link GuiceRemoteServiceServlet} that creates the Guice injector.
 */
public class TestGuiceRemoteServiceServlet extends GuiceRemoteServiceServlet {
  private final Injector injector = Guice.createInjector(new GwtExamplesModule());

  @Override
  protected Injector getInjector() {
    return injector;
  }
}
