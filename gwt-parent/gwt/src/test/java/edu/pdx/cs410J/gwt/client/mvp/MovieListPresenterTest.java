package edu.pdx.cs410J.gwt.client.mvp;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.rmi.Movie;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static edu.pdx.cs410J.gwt.client.mvp.MovieListPresenter.Display;
import static edu.pdx.cs410J.gwt.client.mvp.MovieListPresenter.Display.MovieSelectedHandler;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the logic of the {@link MovieListPresenter}
 */
public class MovieListPresenterTest extends MovieTestCase {

  @Captor
  private ArgumentCaptor<AsyncCallback<ArrayList<Movie>>> allMoviesCaptor;

  @Before
  public void initializeMockito() {
      MockitoAnnotations.initMocks(this);
  }

  /**
   * Tests that all movie titles are displayed when the presenter is created
   */
  @Test
  public void testAllMovieTitlesDisplayed() {
    MovieListPresenter.Display view = mock(MovieListPresenter.Display.class);
    MovieServiceAsync service = mock(MovieServiceAsync.class);
    HandlerManager eventBus = mock(HandlerManager.class);

    new MovieListPresenter(view, service, eventBus);
    verify(service).getAllMovies(allMoviesCaptor.capture());

    List<String> titles = Lists.newArrayList("One", "Two", "Three", "Four");

    List<Movie> movies = Lists.transform(titles, new Function<String, Movie>() {
      @Override
      public Movie apply(String title) {
        return newMovie(title, 2010);

      }
    });
    allMoviesCaptor.getValue().onSuccess(Lists.newArrayList(movies));

    verify(view).setTitles(titles);
  }

  /**
   * Tests that a {@link DisplayMovieEvent} is fired when a movie is selected
   */
  @Test
  public void testDisplayMovieEventFiredOnSelection() {
    Display view = mock(Display.class);
    MovieServiceAsync service = mock(MovieServiceAsync.class);
    HandlerManager eventBus = new HandlerManager(null);

    DisplayMovieEvent.Handler handler = mock(DisplayMovieEvent.Handler.class);
    eventBus.addHandler(DisplayMovieEvent.TYPE, handler);

    Movie movie0 = newMovie("Zero", 2010);
    Movie movie1 = newMovie("One", 2010);
    newPresenter(view, service, eventBus, movie0, movie1);

    ArgumentCaptor<MovieSelectedHandler> captor = ArgumentCaptor.forClass(MovieSelectedHandler.class);
    verify(view).addMovieSelectedHandler(captor.capture());

    captor.getValue().onMovieSelected(0);
    verify(handler).onMovieDisplayed(movie0);

    captor.getValue().onMovieSelected(1);
    verify(handler).onMovieDisplayed(movie1);
  }

  /**
   * Creates a new {@link MovieListPresenter} that displays the given movies
   * @param view The presenter's view
   * @param service The remote service
   * @param eventBus The event bus
   * @param movies The movies to show
   * @return The presenter
   */
  private MovieListPresenter newPresenter(Display view, MovieServiceAsync service, HandlerManager eventBus, Movie... movies) {
    MovieListPresenter presenter = new MovieListPresenter(view, service, eventBus);
    verify(service).getAllMovies(allMoviesCaptor.capture());
    allMoviesCaptor.getValue().onSuccess(Lists.newArrayList(movies));
    return presenter;
  }
}
