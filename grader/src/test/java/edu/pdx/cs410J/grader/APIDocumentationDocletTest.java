package edu.pdx.cs410J.grader;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class APIDocumentationDocletTest {

  @Test
  public void removePackageNameFromJavaLangString() {
    assertThat(APIDocumentationDoclet.removePackageNames("java.lang.String"), equalTo("String"));
  }

  @Test
  public void removePackageNameFromArrayOfJavaLangString() {
    assertThat(APIDocumentationDoclet.removePackageNames("java.lang.String[]"), equalTo("String[]"));
  }

  @Test
  public void removePackageNameFromMapOfStringToString() {
    String fullName = "java.util.Map<java.lang.String,java.lang.String>";
    String shortName = "Map<String,String>";
    assertThat(APIDocumentationDoclet.removePackageNames(fullName), equalTo(shortName));
  }

}
