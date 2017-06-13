package edu.pdx.cs410J.family.android;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import edu.pdx.cs410J.family.Person;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class PersonDAOIT {

  public void databaseIsPrePopulatedWithThreePersons() {
    PersonDAO dao = new PersonDAO(InstrumentationRegistry.getContext());
    Person.Gender gender = Person.Gender.FEMALE;
    Person person = dao.create(gender);

    assertThat(person.getGender(), equalTo(gender));
  }
}
