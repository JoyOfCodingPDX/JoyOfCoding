package edu.pdx.cs.joy.airlineweb;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>Airline</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple dictionary of words
 * and their definitions.
 */
public class AirlineServlet extends HttpServlet {
  static final String WORD_PARAMETER = "word";
  static final String DEFINITION_PARAMETER = "definition";

  private final Map<String, Dictionary<String, String>> dictionaries = new HashMap<>();

  /**
   * Handles an HTTP GET request from a client by writing the definition of the
   * word specified in the "word" HTTP parameter to the HTTP response.  If the
   * "word" parameter is not specified, all of the entries in the dictionary
   * are written to the HTTP response.
   */
  @Override
  protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException {
      response.setContentType( "text/plain" );

    Dictionary<String, String> dictionary = getDictionary(request);
    if (dictionary == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

      String word = getParameter( WORD_PARAMETER, request );
      if (word != null) {
          log("GET " + word);
          writeDefinition(dictionary, word, response);

      } else {
          log("GET all dictionary entries");
          writeAllDictionaryEntries(dictionary, response);
      }
  }

  /**
   * Handles an HTTP POST request by storing the dictionary entry for the
   * "word" and "definition" request parameters.  It writes the dictionary
   * entry to the HTTP response.
   */
  @Override
  protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException
  {
      response.setContentType( "text/plain" );

      String word = getParameter(WORD_PARAMETER, request );
      if (word == null) {
          missingRequiredParameter(response, WORD_PARAMETER);
          return;
      }

      String definition = getParameter(DEFINITION_PARAMETER, request );
      if ( definition == null) {
          missingRequiredParameter( response, DEFINITION_PARAMETER );
          return;
      }

      log("POST " + word + " -> " + definition);

      Dictionary<String, String> dictionary = getDictionary(request);
      if (dictionary == null) {
          response.setStatus(HttpServletResponse.SC_NOT_FOUND);
          return;
      }
      dictionary.put(word, definition);

      PrintWriter pw = response.getWriter();
      pw.println(Messages.definedWordAs(word, definition));
      pw.flush();

      response.setStatus( HttpServletResponse.SC_OK);
  }

  private Dictionary<String, String> getDictionary(HttpServletRequest request) {
    String dictionaryName = getDictionaryName(request);
    if (dictionaryName == null) {
      return null;
    }
    return this.dictionaries.computeIfAbsent(dictionaryName, _ -> new Hashtable<>());
  }

  private String getDictionaryName(HttpServletRequest request) {
    String requestURI = request.getRequestURI();
    Pattern pattern = Pattern.compile("/airline/(.+)/flights");
    Matcher matcher = pattern.matcher(requestURI);
    if (matcher.matches()) {
      return matcher.group(1);

    } else {
      return null;
    }
  }

  /**
   * Handles an HTTP DELETE request by removing all dictionary entries.  This
   * behavior is exposed for testing purposes only.  It's probably not
   * something that you'd want a real application to expose.
   */
  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/plain");

      log("DELETE all dictionary entries");

      this.dictionaries.clear();

      PrintWriter pw = response.getWriter();
      pw.println(Messages.allDictionaryEntriesDeleted());
      pw.flush();

      response.setStatus(HttpServletResponse.SC_OK);

  }

  /**
   * Writes an error message about a missing parameter to the HTTP response.
   *
   * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
   */
  private void missingRequiredParameter( HttpServletResponse response, String parameterName )
      throws IOException
  {
      String message = Messages.missingRequiredParameter(parameterName);
      response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
  }

  /**
   * Writes the definition of the given word to the HTTP response.
   *
   * The text of the message is formatted with {@link TextDumper}
   */
  private void writeDefinition(Dictionary<String, String> dictionary, String word, HttpServletResponse response) throws IOException {
    String definition = dictionary.get(word);

    if (definition == null) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);

    } else {
      PrintWriter pw = response.getWriter();

      Dictionary<String, String> wordDefinition = new Hashtable<>();
      wordDefinition.put(word, definition);
      TextDumper dumper = new TextDumper(pw);
      dumper.dump(wordDefinition);

      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  /**
   * Writes all of the dictionary entries to the HTTP response.
   *
   * The text of the message is formatted with {@link TextDumper}
   */
  private void writeAllDictionaryEntries(Dictionary<String, String> dictionary, HttpServletResponse response ) throws IOException
  {
      PrintWriter pw = response.getWriter();
      TextDumper dumper = new TextDumper(pw);
      dumper.dump(dictionary);

      response.setStatus( HttpServletResponse.SC_OK );
  }

  /**
   * Returns the value of the HTTP request parameter with the given name.
   *
   * @return <code>null</code> if the value of the parameter is
   *         <code>null</code> or is the empty string
   */
  private String getParameter(String name, HttpServletRequest request) {
    String value = request.getParameter(name);
    if (value == null || "".equals(value)) {
      return null;

    } else {
      return value;
    }
  }

  @Override
  public void log(String msg) {
    System.out.println(msg);
  }

  public Dictionary<String, String> getDictionary(String dictionaryName) {
    return this.dictionaries.get(dictionaryName);
  }
}
