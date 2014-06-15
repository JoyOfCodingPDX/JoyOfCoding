package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import edu.pdx.cs410J.rmi.Movie;

/**
 * An event that is fired when a movie is to be displayed in the UI
 */
public class DisplayMovieEvent extends GwtEvent<DisplayMovieEvent.Handler> {

  public interface Handler extends EventHandler {

    /**
     * Invoked when a movie is to be displayed
     * @param movie The movie to display
     */
    void onMovieDisplayed(Movie movie);
  }

  public static final Type<Handler> TYPE = new Type<Handler>();

  private final Movie movie;

  public DisplayMovieEvent(Movie movie) {
    this.movie = movie;
  }

  @Override
  public Type<Handler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(Handler handler) {
    handler.onMovieDisplayed(this.movie);
  }
}
