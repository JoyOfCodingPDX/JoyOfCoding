package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.GenerateStudentInitialsFile.StudentInitials;
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
  void onlyFirstLetterOfNameIsCapitalized() {
    StudentInitials initials = new StudentInitials(new Student("test").setFirstName("Aa").setLastName("Bb"));
    initials.incrementUniqueCharsOfFirstName();
    assertThat(initials.getInitials(), equalTo("AaB"));
    initials.incrementUniqueCharsOfLastName();
    assertThat(initials.getInitials(), equalTo("AaBb"));
  }

  @Test
  void compareStudentInitials() {
    StudentInitials first = new StudentInitials(new Student("first").setFirstName("A").setLastName("A"));
    StudentInitials second = new StudentInitials(new Student("second").setFirstName("A").setLastName("B"));
    assertThat(first.compareTo(second), equalTo(-1));
    assertThat(second.compareTo(first), equalTo(1));
    assertThat(first.getInitials(), equalTo("AA"));
    assertThat(second.getInitials(), equalTo("AB"));

    first = new StudentInitials(new Student("first").setFirstName("A").setLastName("Aa"));
    second = new StudentInitials(new Student("second").setFirstName("A").setLastName("Ab"));
    assertThat(first.compareTo(second), equalTo(-1));
    assertThat(second.compareTo(first), equalTo(1));
    assertThat(first.getInitials(), equalTo("AAa"));
    assertThat(second.getInitials(), equalTo("AAb"));
  }

  @Test
  void useMoreCharactersWhenInitialsMatch() {
    Stream<Student> students = Stream.of(
      new Student("zoe").setFirstName("Zoe").setLastName("Adams"),
      new Student("zelda").setFirstName("Zelda").setLastName("Adams"),
      new Student("larry").setFirstName("Larry").setLastName("Johnson"),
      new Student("larry").setFirstName("Larry").setLastName("Jones")
    );

    List<String> initials = GenerateStudentInitialsFile.generateInitials(students);

    assertThat(initials, hasSize(4));

    Iterator<String> iterator = initials.iterator();
    assertThat(iterator.next(), equalTo("ZeAd"));
    assertThat(iterator.next(), equalTo("ZoAd"));
    assertThat(iterator.next(), equalTo("LaJoh"));
    assertThat(iterator.next(), equalTo("LaJon"));

  }

}
