package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class GenerateStudentInitialsFileTest {

  @Test
  void generatedInitialsAreSortedByLastNameInitial() {
    Stream<Student> students = Stream.of(new Student("dave").setFirstName("Zoe").setLastName("Adams"),
      new Student("alice").setFirstName("Alice").setLastName("Smith"),
      new Student("bob").setFirstName("Bob").setLastName("Johnson"));

    List<String> initials = GenerateStudentInitialsFile.generateInitials(students);

    assertThat(initials, hasSize(3));

    Iterator<String> iterator = initials.iterator();
    assertThat(iterator.next(), equalTo("ZA"));
    assertThat(iterator.next(), equalTo("BJ"));
    assertThat(iterator.next(), equalTo("AS"));
  }

}
