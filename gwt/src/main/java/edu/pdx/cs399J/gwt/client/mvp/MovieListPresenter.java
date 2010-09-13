package edu.pdx.cs399J.gwt.client.mvp;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs399J.rmi.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the logic for displaying a list of {@link Movie}s
 */
public class MovieListPresenter {

  public interface Display {

    void setTitles(List<String> titles);

    void addMovieSelectedHandler(MovieSelectedHandler handler);

    public interface MovieSelectedHandler extends EventHandler {
      /**
       * Invoked when a movie is selected
       * @param index The index of the selected movie
       */
      public void onMovieSelected(int index);
    }
  }

  /** The movies being presented */
  private List<Movie> movies;

  /** The view of this presenter */
  private final Display view;

  private final HandlerManager eventBus;

  public MovieListPresenter(Display view, MovieServiceAsync service, final HandlerManager hm) {
    this.view = view;
    this.eventBus = hm;

    service.getAllMovies(new AsyncCallback<ArrayList<Movie>>() {

      @Override
      public void onFailure(Throwable ex) {
        Window.alert(ex.toString());
      }

      @Override
      public void onSuccess(ArrayList<Movie> movies) {
        setMovies(movies);
      }
    });

    this.view.addMovieSelectedHandler(new Display.MovieSelectedHandler() {

      @Override
      public void onMovieSelected(int index) {
        eventBus.fireEvent(new DisplayMovieEvent(movies.get(index)));
      }
    });
  }

  private void setMovies(List<Movie> movies) {
    this.movies = movies;

    List<String> titles = Lists.transform(movies, new Function<Movie, String>() {
      @Override
      public String apply(Movie movie) {
        return movie.getTitle();
      }
    });

    this.view.setTitles(titles);
  }


}
