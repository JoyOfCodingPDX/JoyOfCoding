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
    Stream<Student> students = Stream.of(
      new Student("larry").setFirstName("Larry").setLastName("Smith"),
    new Student("zoe").setFirstName("Zoe").setLastName("Adams"),
      new Student("alice").setFirstName("Alice").setLastName("Smith")
    );

    List<String> initials = GenerateStudentInitialsFile.generateInitials(students);

    assertThat(initials, hasSize(3));

    Iterator<String> iterator = initials.iterator();
    assertThat(iterator.next(), equalTo("ZA"));
    assertThat(iterator.next(), equalTo("AS"));
    assertThat(iterator.next(), equalTo("LS"));
  }

  @Test
  void useMoreCharactersWhenInitialsMatch() {
    Stream<Student> students = Stream.of(
      new Student("zoe").setFirstName("Zoe").setLastName("Adams"),
      new Student("zelda").setFirstName("Zelda").setLastName("Adams"),
      new Student("larry").setFirstName("Larry").setLastName("Smith"),
      new Student("larry").setFirstName("Larry").setLastName("Smithson")
    );

    List<String> initials = GenerateStudentInitialsFile.generateInitials(students);

    assertThat(initials, hasSize(4));

    Iterator<String> iterator = initials.iterator();
    assertThat(iterator.next(), equalTo("ZoA"));
    assertThat(iterator.next(), equalTo("ZeA"));
    assertThat(iterator.next(), equalTo("LSmith"));
    assertThat(iterator.next(), equalTo("LSmithson"));

  }

}
