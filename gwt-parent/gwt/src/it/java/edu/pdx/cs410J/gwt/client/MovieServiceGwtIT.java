package edu.pdx.cs410J.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.gwt.client.mvp.MovieService;
import edu.pdx.cs410J.gwt.client.mvp.MovieServiceAsync;
import edu.pdx.cs410J.rmi.Movie;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests that the remote movie service is successfully deployed and invokable
 */
public class MovieServiceGwtIT extends IntegrationGwtTestCase {

  /**
   * Tests that we can get all of the movies that we know about
   */
  @Test
  public void testGetAllMovies() {
    MovieServiceAsync service = GWT.create(MovieService.class);
    service.getAllMovies(new AsyncCallback<ArrayList<Movie>>() {

      @Override
      public void onFailure(Throwable throwable) {
        fail(throwable.toString());
        finishTest();
      }

      @Override
      public void onSuccess(ArrayList<Movie> movies) {
        assertFalse(movies.isEmpty());
        finishTest();
      }
    });

    delayTestFinish(1000);
  }
}
