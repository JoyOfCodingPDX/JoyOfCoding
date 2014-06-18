package edu.pdx.cs410J.gwt.server.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import edu.pdx.cs410J.gwt.client.mvp.MovieService;
import edu.pdx.cs410J.gwt.server.mvp.MovieServiceImpl;
import edu.pdx.cs410J.rmi.MovieDatabase;
import edu.pdx.cs410J.rmi.MovieDatabaseImpl;

/**
 * A Guice module for the server side of the GWT examples.  It binds remote interfaces to their implementations.
 */
public class GwtExamplesModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MovieService.class).to(MovieServiceImpl.class).in(Singleton.class);
    bind(MovieDatabase.class).to(MovieDatabaseImpl.class).in(Singleton.class);
    bind(MovieDatabaseInitializer.class).asEagerSingleton();
  }
}
