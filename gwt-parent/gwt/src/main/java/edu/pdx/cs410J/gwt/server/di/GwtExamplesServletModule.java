package edu.pdx.cs410J.gwt.server.di;

import com.google.inject.servlet.ServletModule;

/**
 * A Guice module for configuring servlets used by the GWT examples
 */
public class GwtExamplesServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    serve("/examples/GWT.rpc").with(GuiceRemoteServiceServlet.class);
  }
}
