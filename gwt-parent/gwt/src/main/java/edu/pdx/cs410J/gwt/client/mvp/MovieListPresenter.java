package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import edu.pdx.cs410J.rmi.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the logic for displaying a list of {@link Movie}s
 */
public class MovieListPresenter {

  public interface Display {

    void setTitles(List<String> titles);

    void addMovieSelectedHandler(MovieSelectedHandler handler);

      Widget asWidget();

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

  @Inject
  public MovieListPresenter(Display view, MovieServiceAsync service, final HandlerManager hm) {
    this.view = view;
    this.eventBus = hm;

    service.getAllMovies(new MvpCallback<ArrayList<Movie>>(eventBus) {
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

    List<String> titles = new ArrayList<String>(movies.size());
    for (Movie movie : movies) {
      titles.add(movie.getTitle());
    }

    this.view.setTitles(titles);
  }


}
