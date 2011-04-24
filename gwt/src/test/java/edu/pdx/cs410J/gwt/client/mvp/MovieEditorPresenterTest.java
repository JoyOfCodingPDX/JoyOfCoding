package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.rmi.Movie;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static edu.pdx.cs410J.gwt.client.mvp.MovieEditorPresenter.Display;
import static edu.pdx.cs410J.gwt.client.mvp.MovieEditorPresenter.Display.NewCharacterHandler;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests that the movie editor UI is updated by a {@link MovieEditorPresenter}
 */
public class MovieEditorPresenterTest extends MovieTestCase {

  /**
   * Tests that a movie's title and year are displayed appropriately
   */
  @Test
  public void testTitleAndYear() {
    Display view = mock(Display.class);
    HandlerManager eventBus = new HandlerManager(null);
    MovieServiceAsync service = mock(MovieServiceAsync.class);
    
    new MovieEditorPresenter(view, eventBus, service);

    String title = "Movie 1";
    int year = 2010;

    eventBus.fireEvent(new DisplayMovieEvent(newMovie(title, year)));

    verify(view).setTitle(title);
    verify(view).setYear(year);
  }

  /**
   * Tests that a movie's characters are displayed appropriately
   */
  @Test
  public void testCharacters() {
    Display view = mock(Display.class);
    HandlerManager eventBus = new HandlerManager(null);
    MovieServiceAsync service = mock(MovieServiceAsync.class);

    new MovieEditorPresenter(view, eventBus, service);

    Movie movie = newMovie("Movie 1", 2010);
    addCharacter(movie, "Character 1", 1L);
    eventBus.fireEvent(new DisplayMovieEvent(movie));

    verify(view).setCharacters(movie.getCharacters());
  }

  /**
   * Tests that a new character is created appropriately
   */
  @Test
  public void testNewCharacter() {
    Display view = mock(Display.class);
    HandlerManager eventBus = new HandlerManager(null);
    MovieServiceAsync service = mock(MovieServiceAsync.class);

    new MovieEditorPresenter(view, eventBus, service);

    ArgumentCaptor<NewCharacterHandler> captor = ArgumentCaptor.forClass(NewCharacterHandler.class);
    verify(view).addNewCharacterHandler(captor.capture());

    Movie movie = newMovie("Movie 1", 2010);
    eventBus.fireEvent(new DisplayMovieEvent(movie));

    String character = "Character 1";
    long actorId = 123L;
    captor.getValue().onNewCharacter(character, actorId);

    verify(service).addCharacter(eq(movie.getId()), eq(character), eq(actorId), any(AsyncCallback.class));
  }
}
