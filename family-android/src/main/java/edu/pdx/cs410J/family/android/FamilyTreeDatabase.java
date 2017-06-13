package edu.pdx.cs410J.family.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FamilyTreeDatabase extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "familytree.db";
  private static final int DATABASE_VERSION = 1;

  public FamilyTreeDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    PersonDAO.createDatabaseTable(sqLiteDatabase);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    throw new UnsupportedOperationException("This method is not implemented yet");
  }
}
