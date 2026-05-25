package edu.pdx.cs.joy.grader.canvas;

import com.google.common.annotations.VisibleForTesting;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareCanvasAndWebsiteSchedules {
  static final URI DEFAULT_CANVAS_BASE_URI = URI.create("https://canvas.pdx.edu");
  private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");

  private final HttpClient httpClient;
  private final URI canvasBaseUri;

  public CompareCanvasAndWebsiteSchedules() {
    this(HttpClient.newHttpClient(), DEFAULT_CANVAS_BASE_URI);
  }

  @VisibleForTesting
  CompareCanvasAndWebsiteSchedules(HttpClient httpClient, URI canvasBaseUri) {
    this.httpClient = httpClient;
    this.canvasBaseUri = canvasBaseUri;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    try {
      new CompareCanvasAndWebsiteSchedules().run(args, System.out);

    } catch (IllegalArgumentException ex) {
      usage(ex.getMessage());
    }
  }

  @VisibleForTesting
  void run(String[] args, PrintStream out) throws IOException, InterruptedException {
    String apiTokenFileName = parseApiTokenFileName(args);
    String apiToken = readApiToken(Path.of(apiTokenFileName));

    for (String courseName : getCourseNames(apiToken)) {
      out.println(courseName);
    }
  }

  private static String parseApiTokenFileName(String[] args) {
    String apiTokenFileName = null;

    for (String arg : args) {
      if (apiTokenFileName == null) {
        apiTokenFileName = arg;

      } else {
        throw new IllegalArgumentException("Extraneous command line argument: " + arg);
      }
    }

    if (apiTokenFileName == null) {
      throw new IllegalArgumentException("Missing API token file name");
    }

    return apiTokenFileName;
  }

  private static String readApiToken(Path apiTokenFile) throws IOException {
    if (!Files.exists(apiTokenFile)) {
      throw new IllegalArgumentException("API token file \"" + apiTokenFile + "\" does not exist");
    }

    String apiToken = Files.readString(apiTokenFile).trim();
    if (apiToken.isEmpty()) {
      throw new IllegalArgumentException("API token file \"" + apiTokenFile + "\" is empty");
    }

    return apiToken;
  }

  @VisibleForTesting
  List<String> getCourseNames(String apiToken) throws IOException, InterruptedException {
    List<String> courseNames = new ArrayList<>();
    URI nextPage = getCoursesUri();

    while (nextPage != null) {
      HttpResponse<String> response = invokeCanvas(nextPage, apiToken);
      courseNames.addAll(parseCourseNames(response.body()));
      nextPage = getNextPage(response.headers());
    }

    return courseNames;
  }

  private URI getCoursesUri() {
    return this.canvasBaseUri.resolve("/api/v1/courses?per_page=100");
  }

  private HttpResponse<String> invokeCanvas(URI uri, String apiToken) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder(uri)
      .header("Authorization", "Bearer " + apiToken)
      .header("Accept", "application/json")
      .GET()
      .build();

    HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) {
      throw new IOException("Canvas request to " + uri + " failed with status code " + response.statusCode());
    }

    return response;
  }

  private URI getNextPage(HttpHeaders headers) {
    for (String linkHeader : headers.allValues("Link")) {
      Matcher matcher = NEXT_LINK_PATTERN.matcher(linkHeader);
      if (matcher.find()) {
        return URI.create(matcher.group(1));
      }
    }

    return null;
  }

  @VisibleForTesting
  static List<String> parseCourseNames(String json) {
    JsonCursor cursor = new JsonCursor(json);
    List<String> courseNames = new ArrayList<>();

    cursor.skipWhitespace();
    cursor.expect('[');
    cursor.skipWhitespace();
    if (cursor.consume(']')) {
      return courseNames;
    }

    do {
      String courseName = parseCourseName(cursor);
      if (courseName != null) {
        courseNames.add(courseName);
      }
      cursor.skipWhitespace();
    } while (cursor.consume(','));

    cursor.expect(']');
    cursor.skipWhitespace();
    if (!cursor.isDone()) {
      throw new IllegalArgumentException("Unexpected trailing JSON content");
    }

    return courseNames;
  }

  private static String parseCourseName(JsonCursor cursor) {
    cursor.skipWhitespace();
    cursor.expect('{');
    cursor.skipWhitespace();

    String courseName = null;
    if (cursor.consume('}')) {
      return null;
    }

    do {
      cursor.skipWhitespace();
      String fieldName = cursor.readString();
      cursor.skipWhitespace();
      cursor.expect(':');
      cursor.skipWhitespace();

      if ("name".equals(fieldName) && cursor.peek() == '"') {
        courseName = cursor.readString();

      } else {
        cursor.skipValue();
      }

      cursor.skipWhitespace();
    } while (cursor.consume(','));

    cursor.expect('}');
    return courseName;
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java CompareCanvasAndWebsiteSchedules apiTokenFileName");
    err.println("    apiTokenFileName             File containing the Canvas API token");
    err.println();
    err.println("Prints the names of all courses available to the Canvas API token");
    err.println();

    System.exit(1);
  }

  private static class JsonCursor {
    private final String text;
    private int index;

    private JsonCursor(String text) {
      this.text = text;
    }

    private void skipWhitespace() {
      while (!isDone() && Character.isWhitespace(this.text.charAt(this.index))) {
        this.index++;
      }
    }

    private boolean consume(char expected) {
      if (!isDone() && this.text.charAt(this.index) == expected) {
        this.index++;
        return true;
      }

      return false;
    }

    private void expect(char expected) {
      if (isDone() || this.text.charAt(this.index) != expected) {
        throw new IllegalArgumentException("Expected '" + expected + "' at index " + this.index);
      }

      this.index++;
    }

    private char peek() {
      if (isDone()) {
        throw new IllegalArgumentException("Unexpected end of JSON input");
      }

      return this.text.charAt(this.index);
    }

    private boolean isDone() {
      return this.index >= this.text.length();
    }

    private String readString() {
      expect('"');
      StringBuilder builder = new StringBuilder();

      while (!isDone()) {
        char c = this.text.charAt(this.index++);
        if (c == '"') {
          return builder.toString();
        }

        if (c == '\\') {
          if (isDone()) {
            throw new IllegalArgumentException("Unterminated escape sequence in JSON string");
          }

          char escaped = this.text.charAt(this.index++);
          switch (escaped) {
            case '"':
            case '\\':
            case '/':
              builder.append(escaped);
              break;
            case 'b':
              builder.append('\b');
              break;
            case 'f':
              builder.append('\f');
              break;
            case 'n':
              builder.append('\n');
              break;
            case 'r':
              builder.append('\r');
              break;
            case 't':
              builder.append('\t');
              break;
            case 'u':
              builder.append(readUnicodeEscape());
              break;
            default:
              throw new IllegalArgumentException("Unsupported JSON escape \\" + escaped);
          }

        } else {
          builder.append(c);
        }
      }

      throw new IllegalArgumentException("Unterminated JSON string");
    }

    private char readUnicodeEscape() {
      if (this.index + 4 > this.text.length()) {
        throw new IllegalArgumentException("Incomplete unicode escape sequence");
      }

      String hexDigits = this.text.substring(this.index, this.index + 4);
      this.index += 4;
      return (char) Integer.parseInt(hexDigits, 16);
    }

    private void skipValue() {
      skipWhitespace();
      char c = peek();
      switch (c) {
        case '"':
          readString();
          break;
        case '{':
          skipObject();
          break;
        case '[':
          skipArray();
          break;
        case 't':
          expectLiteral("true");
          break;
        case 'f':
          expectLiteral("false");
          break;
        case 'n':
          expectLiteral("null");
          break;
        default:
          skipNumber();
      }
    }

    private void skipObject() {
      expect('{');
      skipWhitespace();
      if (consume('}')) {
        return;
      }

      do {
        skipWhitespace();
        readString();
        skipWhitespace();
        expect(':');
        skipValue();
        skipWhitespace();
      } while (consume(','));

      expect('}');
    }

    private void skipArray() {
      expect('[');
      skipWhitespace();
      if (consume(']')) {
        return;
      }

      do {
        skipValue();
        skipWhitespace();
      } while (consume(','));

      expect(']');
    }

    private void expectLiteral(String literal) {
      if (!this.text.startsWith(literal, this.index)) {
        throw new IllegalArgumentException("Expected \"" + literal + "\" at index " + this.index);
      }

      this.index += literal.length();
    }

    private void skipNumber() {
      int start = this.index;

      if (consume('-')) {
        // Negative sign already consumed
      }

      while (!isDone() && Character.isDigit(this.text.charAt(this.index))) {
        this.index++;
      }

      if (consume('.')) {
        while (!isDone() && Character.isDigit(this.text.charAt(this.index))) {
          this.index++;
        }
      }

      if (!isDone()) {
        char c = this.text.charAt(this.index);
        if (c == 'e' || c == 'E') {
          this.index++;
          if (!isDone()) {
            char sign = this.text.charAt(this.index);
            if (sign == '+' || sign == '-') {
              this.index++;
            }
          }

          while (!isDone() && Character.isDigit(this.text.charAt(this.index))) {
            this.index++;
          }
        }
      }

      if (start == this.index) {
        throw new IllegalArgumentException("Expected JSON value at index " + this.index);
      }
    }
  }
}
