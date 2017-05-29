package edu.pdx.cs410J.family.android;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.Arrays;
import java.util.List;

public class PersonListActivity extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    List<String> items = Arrays.asList("One", "Two", "Three");
    ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
    setListAdapter(adapter);
  }

}
