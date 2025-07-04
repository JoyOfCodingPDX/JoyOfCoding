package edu.pdx.cs.joy.grader;

import edu.pdx.cs.joy.grader.gradebook.Student;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GenerateStudentInitialsFileTest {

  @Test
  void computeInitialsForAStudent() {
    Student student = new Student("dave")
      .setFirstName("David")
      .setLastName("Whitlock");

    assertThat(GenerateStudentInitialsFile.computeInitials(student), equalTo("DW"));
  }

}
