package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import javax.lang.model.element.ElementKind;
import java.io.PrintWriter;
import java.io.StringWriter;

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

  @Test
  public void classHeaderUsesLowerCaseClass() {
    assertThat(APIDocumentationDoclet.classHeader(ElementKind.CLASS, "edu.pdx.cs.joy.grader.Foo"),
      equalTo("class edu.pdx.cs.joy.grader.Foo"));
  }

  @Test
  public void classHeaderUsesEnumForEnums() {
    assertThat(APIDocumentationDoclet.classHeader(ElementKind.ENUM, "edu.pdx.cs.joy.grader.Color"),
      equalTo("enum edu.pdx.cs.joy.grader.Color"));
  }

  @Test
  public void classHeaderUsesRecordForRecords() {
    assertThat(APIDocumentationDoclet.classHeader(ElementKind.RECORD, "edu.pdx.cs.joy.grader.Measurement"),
      equalTo("record edu.pdx.cs.joy.grader.Measurement"));
  }

  @Test
  public void leadingWhitespaceIsRemovedWhenIndenting() {
    String rawText = "classes - The names of the classes the student is taking.  A student              \n" +
      "        may take zero or more classes.";
    String expected = "    classes - The names of the classes the student is taking. A student\n" +
                      "    may take zero or more classes.\n";
    assertThat(indentString(rawText, 4), equalTo(expected));
  }

  private String indentString(String rawText, int indent) {
    StringWriter sw = new StringWriter();
    APIDocumentationDoclet.indent(rawText, indent, new PrintWriter(sw, true));
    return sw.toString();
  }

  @Test
  public void noSpaceAfterDotInMiddleOfWord() {
    assertThat(indentString("1.2.3", 2), equalTo("  1.2.3\n"));
  }

  @Test
  public void spacesAfterPeriodAreCollapsedIntoOneSpace() {
    String rawText = "One sentence.  Two sentences.";
    String expected = "  One sentence. Two sentences.\n";
    assertThat(indentString(rawText, 2), equalTo(expected));
  }

}
