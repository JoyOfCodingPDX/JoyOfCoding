package edu.pdx.cs410J.grader;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.util.DocTrees;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This <A
 * href="https://docs.oracle.com/javase/9/docs/api/jdk/javadoc/doclet/package-summary.html">doclet</A>
 * extracts the API documentation (Javadocs)
 * from a student's project submission and produces a text summary of
 * them.  It is used for grading a student's Javadocs.
 *
 * @author David Whitlock
 * @since Summer 2004 (rewritten for Java 9 API in Summer 2018)
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

  /**
   * Indents a block of text a given amount.
   */
  private static void indent(String text, final int indent,
                             PrintWriter pw) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < indent; i++) {
      sb.append(" ");
    }
    String spaces = sb.toString();

    pw.print(spaces);

    int printed = indent;
    boolean firstWord = true;

    BreakIterator boundary = BreakIterator.getWordInstance();
    boundary.setText(text);
    int start = boundary.first();
    for (int end = boundary.next(); end != BreakIterator.DONE;
         start = end, end = boundary.next()) {

      String word = text.substring(start, end);

      if (printed + word.length() > 72) {
        pw.println("");
        pw.print(spaces);
        printed = indent;
        firstWord = true;
      }

      if (word.charAt(word.length() - 1) == '\n') {
        pw.write(word, 0, word.length() - 1);

      } else if (firstWord &&
                 Character.isWhitespace(word.charAt(0))) {
        pw.write(word, 1, word.length() - 1);

      } else {
        pw.print(word);
      }
      printed += (end - start);
      firstWord = false;
    }

    pw.println("");
  }
  public void printElement(DocTrees trees, Element e) {
    DocCommentTree docCommentTree = trees.getDocCommentTree(e);
    if (docCommentTree != null) {
      System.out.println("Element (" + e.getKind() + ": "
        + e + ") has the following comments:");
      System.out.println("Entire body: " + docCommentTree.getFullBody());
      System.out.println("Block tags: " + docCommentTree.getBlockTags());
    }
  }

  @Override
  public boolean run(DocletEnvironment environment) {
    reporter.print(Diagnostic.Kind.NOTE, "Doclet.run()");

    PrintWriter pw = new PrintWriter(System.out, true);

    DocTrees docTrees = environment.getDocTrees();
    for (TypeElement aClass : ElementFilter.typesIn(environment.getIncludedElements())) {
      generateClassDocumentation(docTrees, aClass, pw);
    }
    return true;
  }

  private void generateClassDocumentation(DocTrees docTrees, TypeElement aClass, PrintWriter pw) {
    pw.println("Class " + aClass.getQualifiedName());

    indent(getFullBodyComment(docTrees, aClass), 2, pw);
    pw.println("");

    Stream<? extends Element> constructors = aClass.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.CONSTRUCTOR);
    constructors.forEach(constructor -> {
      generateMethodDocumentation(docTrees, (ExecutableElement) constructor, pw);
    });

    Stream<? extends Element> methods = aClass.getEnclosedElements().stream().filter(e -> e.getKind() == ElementKind.METHOD);
    methods.forEach(method -> {
      generateMethodDocumentation(docTrees, (ExecutableElement) method, pw);
    });


    for (Element e : aClass.getEnclosedElements()) {
      printElement(docTrees, e);
    }
  }

  private void generateMethodDocumentation(DocTrees docTrees, ExecutableElement m, PrintWriter pw) {
    StringBuilder sb = new StringBuilder();
    sb.append(joinObjectsToStrings(m.getModifiers(), " ")).append(" ");
    appendTypeVariables(m, sb);
    appendReturnType(m, sb);
    sb.append(getMethodOrConstructorName(m));
    sb.append("(");
    sb.append(m.getParameters().stream().map(this::getParameterTypeAndName).collect(Collectors.joining(", ")));
    sb.append(")");

    indent(sb.toString(), 2, pw);
  }

  private String getParameterTypeAndName(VariableElement parameter) {
    return parameter.asType() +
    " " +
    parameter.getSimpleName();
  }

  private Name getMethodOrConstructorName(ExecutableElement method) {
    if (method.getKind() == ElementKind.CONSTRUCTOR) {
      return method.getEnclosingElement().getSimpleName();

    } else {
      return method.getSimpleName();
    }
  }

  private void appendReturnType(Element element, StringBuilder sb) {
    if (element instanceof ExecutableElement) {
      ExecutableElement method = (ExecutableElement) element;
      appendType(method.getReturnType(), sb).append(" ");
    }
  }

  private StringBuilder appendType(TypeMirror type, StringBuilder sb) {
    sb.append(type.toString());

    return sb;
  }

  private void appendTypeVariables(Element constructor, StringBuilder sb) {
    // Not sure how to do this yet
  }

  private String getFullBodyComment(DocTrees docTrees, TypeElement element) {
    DocCommentTree commentTree = docTrees.getDocCommentTree(element);
    if (commentTree == null) {
      return "";

    } else {
      return joinObjectsToStrings(commentTree.getFullBody(), "");
    }
  }

  private String joinObjectsToStrings(Collection<?> objects, String delimiter) {
    return objects.stream().map(Object::toString).collect(Collectors.joining(delimiter));
  }
}
