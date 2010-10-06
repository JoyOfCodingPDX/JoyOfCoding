package edu.pdx.cs399J.gwt.server.di;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContextEvent;

/**
 * A servlet context listener that creates the Guice {@link Injector} for the GWT examples
 */
public class GwtExamplesServletContextListener extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new GwtExamplesModule(), new GwtExamplesServletModule());
  }
}
