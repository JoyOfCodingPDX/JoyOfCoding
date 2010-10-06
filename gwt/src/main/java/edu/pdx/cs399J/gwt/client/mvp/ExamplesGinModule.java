package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.GinModule;
import com.google.gwt.inject.client.binder.GinBinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import edu.pdx.cs399J.gwt.client.DivisionService;

/**
 * A gin module that binds the well-known MVP example objects
 */
public class ExamplesGinModule extends AbstractGinModule {
  @Override
  protected void configure() {
//    bind(DivisionPresenter.Display.class).to(DivisionView.class).in(Singleton.class);
//    bind(DivisionPresenter.class).asEagerSingleton();
    
    bind(ExceptionPresenter.Display.class).to(ExceptionDialog.class).in(Singleton.class);
    bind(ExceptionPresenter.class).asEagerSingleton();

    bind(MovieListPresenter.Display.class).to(MovieListView.class).in(Singleton.class);
    bind(MovieListPresenter.class).asEagerSingleton();
  }

  @Provides
  @Singleton
  public HandlerManager provideEventBus() {
    return new HandlerManager(null);
  }
}
