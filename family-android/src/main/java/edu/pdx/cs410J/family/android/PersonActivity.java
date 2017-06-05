package edu.pdx.cs410J.family.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class PersonActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.person);

    TextView text = (TextView) findViewById(R.id.name);
    Uri uri = getIntent().getData();
    text.setText(uri.toString());
  }
}
