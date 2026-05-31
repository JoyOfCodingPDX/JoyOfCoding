package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.DocumentationTool;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class APIDocumentationDocletIT {

  @Test
  void enumSyntheticMethodsAreNotDocumented(@TempDir Path tempDir) throws IOException {
    String output = runDocletAgainst(tempDir, "Color.java", """
      package edu.pdx.cs.joy.grader.fixtures;

      /** Docs for Color. */
      public enum Color {
        RED;

        /** Explicit helper. */
        public String label() {
          return name();
        }
      }
      """);

    assertThat(output, containsString("Class edu.pdx.cs.joy.grader.fixtures.Color"));
    assertThat(output, containsString("label()"));
    assertThat(output, not(containsString("Color()")));
    assertThat(output, not(containsString("values()")));
    assertThat(output, not(containsString("valueOf(String name)")));
  }

  @Test
  void recordSyntheticAccessorIsNotDocumented(@TempDir Path tempDir) throws IOException {
    String output = runDocletAgainst(tempDir, "Measurement.java", """
      package edu.pdx.cs.joy.grader.fixtures;

      /** Docs for Measurement. */
      public record Measurement(int value) {

        /** Explicit helper. */
        public int doubled() {
          return this.value * 2;
        }
      }
      """);

    assertThat(output, containsString("Class edu.pdx.cs.joy.grader.fixtures.Measurement"));
    assertThat(output, containsString("doubled()"));
    assertThat(output, not(containsString("Measurement(int value)")));
    assertThat(output, not(containsString("toString()")));
    assertThat(output, not(containsString("hashCode()")));
    assertThat(output, not(containsString("equals(Object o)")));
    assertThat(output, not(containsString("value()")));
  }

  private String runDocletAgainst(Path tempDir, String fileName, String sourceCode) throws IOException {
    Path packageDir = tempDir.resolve(Path.of("edu", "pdx", "cs", "joy", "grader", "fixtures"));
    Files.createDirectories(packageDir);

    Path sourceFile = packageDir.resolve(fileName);
    Files.writeString(sourceFile, sourceCode);

    DocumentationTool javadoc = ToolProvider.getSystemDocumentationTool();
    assertThat("Javadoc tool should be available", javadoc, notNullValue());

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;

    try (StandardJavaFileManager fileManager = javadoc.getStandardFileManager(null, null, null)) {
      Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjects(sourceFile.toFile());
      DocumentationTool.DocumentationTask task =
        javadoc.getTask(new StringWriter(), fileManager, null, APIDocumentationDoclet.class, List.of("-quiet"), sources);

      System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
      boolean succeeded = task.call();
      assertThat("Javadoc task should succeed", succeeded, equalTo(true));
      return out.toString(StandardCharsets.UTF_8);

    } finally {
      System.setOut(originalOut);
    }
  }
}
