package edu.pdx.cs.joy.grader.canvas;

import com.google.common.annotations.VisibleForTesting;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareCanvasAndWebsiteSchedules {
  static final URI DEFAULT_CANVAS_BASE_URI = URI.create("https://canvas.pdx.edu");
  private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");

  private final HttpClient httpClient;
  private final URI canvasBaseUri;
  private final Clock clock;

  public CompareCanvasAndWebsiteSchedules() {
    this(HttpClient.newHttpClient(), DEFAULT_CANVAS_BASE_URI, Clock.systemUTC());
  }

  @VisibleForTesting
  CompareCanvasAndWebsiteSchedules(HttpClient httpClient, URI canvasBaseUri) {
    this(httpClient, canvasBaseUri, Clock.systemUTC());
  }

  @VisibleForTesting
  CompareCanvasAndWebsiteSchedules(HttpClient httpClient, URI canvasBaseUri, Clock clock) {
    this.httpClient = httpClient;
    this.canvasBaseUri = canvasBaseUri;
    this.clock = clock;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    try {
      new CompareCanvasAndWebsiteSchedules().run(args, System.out);

    } catch (IllegalArgumentException ex) {
      usage(ex.getMessage());

    } catch (IllegalStateException ex) {
      error(ex.getMessage());
    }
  }

  @VisibleForTesting
  void run(String[] args, PrintStream out) throws IOException, InterruptedException {
    String apiTokenFileName = parseApiTokenFileName(args);
    String apiToken = readApiToken(Path.of(apiTokenFileName));

    CanvasCourse course = getNextCourse(apiToken);
    for (CanvasAssignment assignment : getAssignments(apiToken, course)) {
      out.println(assignment.name() + ": " + assignment.dueDateAsText());
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
  CanvasCourse getNextCourse(String apiToken) throws IOException, InterruptedException {
    List<CanvasCourse> courses = new ArrayList<>();
    URI nextPage = getCoursesUri();

    while (nextPage != null) {
      HttpResponse<String> response = invokeCanvas(nextPage, apiToken);
      courses.addAll(parseCourses(response.body()));
      nextPage = getNextPage(response.headers());
    }

    Instant now = this.clock.instant();
    Optional<CanvasCourse> nextCourse = courses.stream()
      .filter(course -> course.startAt().isAfter(now))
      .min((left, right) -> left.startAt().compareTo(right.startAt()));

    return nextCourse
      .orElseThrow(() -> new IllegalStateException("No upcoming Canvas course offerings found"));
  }

  @VisibleForTesting
  List<CanvasAssignment> getAssignments(String apiToken, CanvasCourse course) throws IOException, InterruptedException {
    List<CanvasAssignment> assignments = new ArrayList<>();
    URI nextPage = getAssignmentsUri(course);

    while (nextPage != null) {
      HttpResponse<String> response = invokeCanvas(nextPage, apiToken);
      assignments.addAll(parseAssignments(response.body()));
      nextPage = getNextPage(response.headers());
    }

    return assignments;
  }

  private URI getCoursesUri() {
    return this.canvasBaseUri.resolve("/api/v1/courses?per_page=100");
  }

  private URI getAssignmentsUri(CanvasCourse course) {
    return this.canvasBaseUri.resolve("/api/v1/courses/" + course.id() + "/assignments?per_page=100");
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
  static List<CanvasCourse> parseCourses(String json) {
    List<CanvasCourse> courses = new ArrayList<>();
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      JsonArray jsonCourses = reader.readArray();
      for (JsonValue courseValue : jsonCourses) {
        if (courseValue.getValueType() != JsonValue.ValueType.OBJECT) {
          continue;
        }

        JsonObject course = courseValue.asJsonObject();
        JsonValue id = course.get("id");
        JsonValue name = course.get("name");
        JsonValue startAt = course.get("start_at");
        if (id instanceof JsonNumber && name instanceof JsonString && startAt instanceof JsonString) {
          courses.add(new CanvasCourse(
            ((JsonNumber) id).intValue(),
            ((JsonString) name).getString(),
            OffsetDateTime.parse(((JsonString) startAt).getString()).toInstant()));
        }
      }

      return courses;
    }
  }

  @VisibleForTesting
  static List<CanvasAssignment> parseAssignments(String json) {
    List<CanvasAssignment> assignments = new ArrayList<>();
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      JsonArray jsonAssignments = reader.readArray();
      for (JsonValue assignmentValue : jsonAssignments) {
        if (assignmentValue.getValueType() != JsonValue.ValueType.OBJECT) {
          continue;
        }

        JsonObject assignment = assignmentValue.asJsonObject();
        JsonValue name = assignment.get("name");
        if (!(name instanceof JsonString)) {
          continue;
        }

        JsonValue dueAt = assignment.get("due_at");
        Optional<LocalDate> dueDate = Optional.empty();
        if (dueAt instanceof JsonString) {
          dueDate = Optional.of(OffsetDateTime.parse(((JsonString) dueAt).getString()).toLocalDate());
        }

        assignments.add(new CanvasAssignment(((JsonString) name).getString(), dueDate));
      }

      return assignments;
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java CompareCanvasAndWebsiteSchedules apiTokenFileName");
    err.println("    apiTokenFileName             File containing the Canvas API token");
    err.println();
    err.println("Prints assignment due dates for the Canvas course offering that will start next");
    err.println();

    System.exit(1);
  }

  private static void error(String message) {
    System.err.println("+++ " + message);
    System.exit(1);
  }

  @VisibleForTesting
  static record CanvasCourse(int id, String name, Instant startAt) {
  }

  @VisibleForTesting
  static record CanvasAssignment(String name, Optional<LocalDate> dueDate) {
    String dueDateAsText() {
      return this.dueDate.map(LocalDate::toString).orElse("(no due date)");
    }
  }
}
