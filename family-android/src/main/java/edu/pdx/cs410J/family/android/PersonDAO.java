package edu.pdx.cs410J.family.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.pdx.cs410J.family.Person;

public class PersonDAO {
  private static final String TABLE_NAME = "persons";

  private static final String COLUMN_ID = "_id";
  private static final String COLUMN_GENDER = "gender";
  private static final String COLUMN_FIRST_NAME = "firstName";

  private static final String[] ALL_COLUMNS = new String[] {
    COLUMN_ID, COLUMN_GENDER, COLUMN_FIRST_NAME
  };

  private static final String DATABASE_CREATE =
    "create table " + TABLE_NAME + "( " +
      COLUMN_ID + " integer primary key autoincrement, " +
      COLUMN_GENDER + " text not null," +
      COLUMN_FIRST_NAME + " text" +
    ");";

  private final FamilyTreeDatabase familyTreeDatabase;
  private SQLiteDatabase database;

  PersonDAO(Context context) {
    this(new FamilyTreeDatabase(context));
  }

  PersonDAO(FamilyTreeDatabase familyTreeDatabase) {
    this.familyTreeDatabase = familyTreeDatabase;
  }

  public void open() {
    this.database = familyTreeDatabase.getWritableDatabase();
  }

  public void close() {
    this.database.close();
  }

  public Person create(Person.Gender gender) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_GENDER, asString(gender));
    long id = database.insert(TABLE_NAME, null, values);
    Cursor cursor = database.query(TABLE_NAME, ALL_COLUMNS,
      COLUMN_ID + " = " + id, null,
      null, null, null);
    cursor.moveToFirst();

    Person person = newPerson(cursor);
    cursor.close();
    return person;
  }

  private String asString(Person.Gender gender) {
    switch (gender) {
      case FEMALE:
        return "FEMALE";
      case MALE:
        return "MALE";
      case UNKNOWN:
        return "UNKNOWN";
      default:
        String s = "Don't know how to convert a Gender of \"" + gender + "\" to a String";
        throw new IllegalStateException(s);
    }
  }

  private Person newPerson(Cursor cursor) {
    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
    Person.Gender gender = asGender(cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)));

    Person person = new Person(id, gender);
    return person;
  }

  private Person.Gender asGender(String string) {
    switch (string) {
      case "FEMALE":
        return Person.Gender.FEMALE;
      case "MALE":
        return Person.Gender.MALE;
      case "UNKNOWN":
        return Person.Gender.UNKNOWN;
      default:
        String s = "Don't know how to convert \"" + string + "\" to a Gender";
        throw new IllegalStateException(s);
    }
  }

  public static void createDatabaseTable(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(DATABASE_CREATE);
  }
}
