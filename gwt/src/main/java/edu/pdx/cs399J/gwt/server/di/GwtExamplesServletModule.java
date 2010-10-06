package edu.pdx.cs399J.gwt.server.di;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import edu.pdx.cs399J.gwt.client.mvp.MovieService;
import edu.pdx.cs399J.gwt.server.DivisionServiceImpl;
import edu.pdx.cs399J.gwt.server.mvp.MovieServiceImpl;

/**
 * A Guice module for configuring servlets used by the GWT examples
 */
public class GwtExamplesServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    serve("/examples/GWT.rpc").with(GuiceRemoteServiceServlet.class);
  }
}
