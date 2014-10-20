package edu.pdx.cs410J.grader;

import com.sun.javadoc.*;
import java.io.*;
import java.text.BreakIterator;

/**
 * This <A
 * href="http://java.sun.com/j2se/1.4.2/docs/tooldocs/javadoc/overview.html">doclet</A>
 * extracts the API documentation (Javadocs)
 * from a student's project submission and produces a text summary of
 * them.  It is used for grading a student's Javadocs.
 *
 * @author David Whitlock
 * @since Summer 2004
 */
@SuppressWarnings("UnusedDeclaration")
public class APIDocumentationDoclet {

  /**
   * NOTE: Without this method present and returning LanguageVersion.JAVA_1_5,
   * Javadoc will not process generics because it assumes LanguageVersion.JAVA_1_1.
   *
   * Note that 1.5 is as high as the <code>LanguageVersion</code> goes.
   *
   * @return language version (hard coded to LanguageVersion.JAVA_1_5)
   */
  public static LanguageVersion languageVersion() {
    return LanguageVersion.JAVA_1_5;
  }

  /**
   * This doclet has no valid options
   */
  public static boolean validOptions(String[][] options,
                                     DocErrorReporter reporter) {
    return true;
  }


  /**
   * Since there are no options, we always return zero.
   */
  public static int optionLength(String option) {
    return 0;
  }

  /**
   * Print out a summary of each class, field, and method to standard
   * out.
   */
  public static boolean start(RootDoc root) {
    PrintWriter pw = new PrintWriter(System.out, true);

    ClassDoc[] classes = root.classes();
    for (ClassDoc aClass : classes) {
      generate(aClass, pw);
      pw.println("");
    }

    return true;
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

  /**
   * Generates a summary of the API documentation for a given class
   */
  private static void generate(ClassDoc c, PrintWriter pw) {
    pw.println("Class " + c.qualifiedTypeName());
    indent(c.commentText(), 2, pw);
    pw.println("");

    ConstructorDoc[] constructors = c.constructors();
    for (ConstructorDoc constructor : constructors) {
      generate(constructor, pw);
    }

    MethodDoc[] methods = c.methods();
    for (MethodDoc method : methods) {
      generate(method, pw);
    }
  }

  /**
   * Generates a summary of the API documentation for a method or
   * constructor
   */
  private static void generate(ExecutableMemberDoc m, PrintWriter pw) {
    StringBuilder sb = new StringBuilder();
    sb.append(m.modifiers()).append(" ");
    appendTypeVariables(m.typeParameters(), sb);
    appendReturnType(m, sb);
    sb.append(m.name());
    sb.append("(");
    Parameter[] params = m.parameters();
    for (int i = 0; i < params.length; i++) {
      Parameter param = params[i];
      sb.append(param.typeName());
      sb.append(" ");
      sb.append(param.name());

      if (i < params.length - 1) {
        sb.append(", ");
      }
    }
    sb.append(")");

    indent(sb.toString(), 2, pw);

    String comment = m.commentText();
    if (comment != null && !comment.equals("")) {
      indent(comment, 4, pw);
    }
    pw.println("");

    ParamTag[] tags = m.paramTags();
    for (ParamTag tag : tags) {
      indent(tag.parameterName() + " - " + tag.parameterComment(), 4, pw);
      pw.println("");
    }

    ThrowsTag[] throwsTags = m.throwsTags();
    for (ThrowsTag tag : throwsTags) {
      indent("throws " + tag.exceptionName() + " - " +
        tag.exceptionComment(), 4, pw);
      pw.println("");
    }

  }

  private static void appendTypeVariables(TypeVariable[] variables, StringBuilder sb) {
    if (variables.length > 0) {
      sb.append("<");

      for (int i = 0; i < variables.length; i++) {
        TypeVariable variable = variables[i];
        appendType(variable, sb);

        if (i < variables.length - 1) {
          sb.append(", ");
        }
      }

      sb.append("> ");
    }
  }

  private static void appendReturnType(ExecutableMemberDoc m, StringBuilder sb) {
    if (m instanceof MethodDoc) {
      MethodDoc method = (MethodDoc) m;
      appendType(method.returnType(), sb).append(" ");
    }
  }

  private static StringBuilder appendType(Type type, StringBuilder sb) {
    sb.append(type.qualifiedTypeName());

    ParameterizedType parameterizedType = type.asParameterizedType();
    if (parameterizedType != null) {
      appendTypeArguments(parameterizedType.typeArguments(), sb);
    }

    return sb;
  }

  private static void appendTypeArguments(Type[] typeArguments, StringBuilder sb) {
    if (typeArguments.length >0) {
      sb.append("<");
      for (int i = 0; i < typeArguments.length; i++) {
        Type typeArgument = typeArguments[i];
        sb.append(typeArgument.qualifiedTypeName());

        if (i < typeArguments.length - 1) {
          sb.append(", ");
        }
      }
      sb.append(">");
    }
  }

}
