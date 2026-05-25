package edu.pdx.cs.joy.grader.canvas;

import com.google.common.annotations.VisibleForTesting;
import jakarta.json.Json;
import jakarta.json.JsonArray;
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

    out.println(getNextCourseName(apiToken));
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
  String getNextCourseName(String apiToken) throws IOException, InterruptedException {
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
      .map(CanvasCourse::name)
      .orElseThrow(() -> new IllegalStateException("No upcoming Canvas course offerings found"));
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
  static List<CanvasCourse> parseCourses(String json) {
    List<CanvasCourse> courses = new ArrayList<>();
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      JsonArray jsonCourses = reader.readArray();
      for (JsonValue courseValue : jsonCourses) {
        if (courseValue.getValueType() != JsonValue.ValueType.OBJECT) {
          continue;
        }

        JsonObject course = courseValue.asJsonObject();
        JsonValue name = course.get("name");
        JsonValue startAt = course.get("start_at");
        if (name instanceof JsonString && startAt instanceof JsonString) {
          courses.add(new CanvasCourse(
            ((JsonString) name).getString(),
            OffsetDateTime.parse(((JsonString) startAt).getString()).toInstant()));
        }
      }

      return courses;
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;

    err.println("+++ " + message);
    err.println();
    err.println("usage: java CompareCanvasAndWebsiteSchedules apiTokenFileName");
    err.println("    apiTokenFileName             File containing the Canvas API token");
    err.println();
    err.println("Prints the name of the Canvas course offering that will start next");
    err.println();

    System.exit(1);
  }

  private static void error(String message) {
    System.err.println("+++ " + message);
    System.exit(1);
  }

  @VisibleForTesting
  static record CanvasCourse(String name, Instant startAt) {
  }
}
