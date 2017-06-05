package edu.pdx.cs410J.family.android;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class PersonListActivity extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.personlist);

    List<String> items = Arrays.asList("One", "Two", "Three");
    ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
    setListAdapter(adapter);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);  // Highlight button text, etc.
    Log.i("PersonListActivity", "clicked item " + position);

    Intent intent = new Intent(this, PersonActivity.class);
    intent.setAction(Intent.ACTION_VIEW);
    Uri.Builder builder = new Uri.Builder();
    builder.scheme("family");
    builder.appendPath("person").appendPath(String.valueOf(position));
    intent.setData(builder.build());
    startActivity(intent);
  }
}
