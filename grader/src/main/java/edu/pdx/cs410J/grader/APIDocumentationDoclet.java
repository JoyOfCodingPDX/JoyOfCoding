package edu.pdx.cs410J.grader;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * This <A
 * href="https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/package-summary.html">doclet</A>
 * extracts the API documentation (Javadocs)
 * from a student's project submission and produces a text summary of
 * them.  It is used for grading a student's Javadocs.
 *
 * @author David Whitlock
 * @since Summer 2004 (rewritten for Java 6 in Summer 2018)
 */

public class APIDocumentationDoclet implements Doclet {

  private Reporter reporter;

  @Override
  public void init(Locale locale, Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public String getName() {
    return "API Documentation Doclet";
  }

  @Override
  public Set<? extends Option> getSupportedOptions() {
    return new HashSet<>();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_9;
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    reporter.print(Diagnostic.Kind.NOTE, "Doclet.run()");

    return true;
  }
}
