package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import com.google.inject.Inject;
import edu.pdx.cs410J.rmi.Movie;

import java.util.Map;               

/**
 * The presenter that edits information about a {@link Movie}
 */
public class MovieEditorPresenter {
  public interface Display {

    void setTitle(String title);

    void setYear(int year);

    void setCharacters(Map<String, Long> characters);

    public interface NewCharacterHandler {
      public void onNewCharacter(String character, long actorId);
    }

    void addNewCharacterHandler(NewCharacterHandler handler);
  }

  private final Display view;

  private final HandlerManager eventBus;

  private Movie movie;

  @Inject
  public MovieEditorPresenter(Display view, HandlerManager hm, final MovieServiceAsync service) {
    this.view = view;
    this.eventBus = hm;

    eventBus.addHandler(DisplayMovieEvent.TYPE, new DisplayMovieEvent.Handler() {

      @Override
      public void onMovieDisplayed(Movie movie) {
        edit(movie);
      }
    });

    view.addNewCharacterHandler(new Display.NewCharacterHandler() {

      @Override
      public void onNewCharacter(String character, long actorId) {
        final HandlerManager eventBus1 = eventBus;
        service.addCharacter(movie.getId(), character, actorId, new MvpCallback<Movie>(eventBus1) {
          @Override
          public void onSuccess(Movie movie) {
            edit(movie);
          }
        });
      }
    });
  }

  private void edit(Movie movie) {
    this.movie = movie;

    view.setTitle(movie.getTitle());
    view.setYear(movie.getYear());
    view.setCharacters(movie.getCharacters());
  }


}
