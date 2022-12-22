package edu.pdx.cs410J.rmi;

import com.google.common.collect.Iterables;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodName.class)
public class MovieDatabaseRmiMainTest extends MovieDatabaseRmiTestCase {

  @Test
  public void test1CreateMovie() throws RemoteException, NotBoundException {
    String title = "Black Panther";
    int year = 2018;

    MainMethodResult result = invokeMain(CreateMovie.class, RMI_HOST, String.valueOf(rmiPort), title, String.valueOf(year));
    assertThat(result.getTextWrittenToStandardOut(), containsString("Created movie"));
    assertThat(result.getTextWrittenToStandardError(), equalTo(""));

    MovieDatabase database = getMovieDatabase();
    Collection<Movie> movies = database.getMovies();
    assertThat(movies.size(), equalTo(1));
    Movie movie = Iterables.getOnlyElement(movies);
    assertThat(movie.getTitle(), equalTo(title));
    assertThat(movie.getYear(), equalTo(year));
  }

}
