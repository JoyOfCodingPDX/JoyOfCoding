package edu.pdx.cs410J.gwt.client.mvp;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * The UI for the list of movies.
 */
public class MovieListView extends Composite implements MovieListPresenter.Display {

  private final ListBox movies;

  public MovieListView() {
    this.movies = new ListBox();
    this.movies.setVisibleItemCount(10);
    initWidget(this.movies);
  }

  @Override
  public void setTitles(List<String> titles) {
    movies.clear();
    for (String title : titles) {
      movies.addItem(title);
    }
  }

  @Override
  public void addMovieSelectedHandler(final MovieSelectedHandler handler) {
    movies.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent changeEvent) {
        handler.onMovieSelected(movies.getSelectedIndex());
      }
    });
  }

    @Override
    public Widget asWidget() {
        return this;
    }
}
