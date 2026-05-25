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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;

public class CompareCanvasAndWebsiteSchedules {
  static final URI DEFAULT_CANVAS_BASE_URI = URI.create("https://canvas.pdx.edu");
  private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");
  private static final DateTimeFormatter WEBSITE_DATE_FORMAT =
    DateTimeFormatter.ofPattern("MMMM d, uuuu", Locale.US);
  private static final ZoneId PACIFIC_TIME_ZONE = ZoneId.of("America/Los_Angeles");

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
    String websiteJsonFileName = parseWebsiteJsonFileName(args);
    String apiToken = readApiToken(Path.of(apiTokenFileName));
    CanvasCourse course = getNextCourse(apiToken);
    List<CanvasAssignment> canvasAssignments = getAssignments(apiToken, course);

    String websiteJson = readWebsiteJson(Path.of(websiteJsonFileName));
    List<WebsiteAssignment> websiteAssignments = parseWebsiteAssignments(websiteJson);

    out.println("Canvas:");
    for (CanvasAssignment assignment : canvasAssignments) {
      out.println(assignment.name() + ": " + assignment.dueDateAsText());
    }
    out.println();
    out.println("Website:");
    for (WebsiteAssignment assignment : websiteAssignments) {
      out.println(assignment.name() + ": " + assignment.dueDateAsText());
    }
  }

  private static String parseApiTokenFileName(String[] args) {
    if (args.length == 0) {
      throw new IllegalArgumentException("Missing API token file name");
    }

    return args[0];
  }

  private static String parseWebsiteJsonFileName(String[] args) {
    if (args.length < 2) {
      throw new IllegalArgumentException("Missing website JSON file name");
    }

    if (args.length > 2) {
      throw new IllegalArgumentException("Extraneous command line argument: " + args[2]);
    }

    return args[1];
  }

  private static String readWebsiteJson(Path websiteJsonFile) throws IOException {
    if (!Files.exists(websiteJsonFile)) {
      throw new IllegalArgumentException("Website JSON file \"" + websiteJsonFile + "\" does not exist");
    }

    return Files.readString(websiteJsonFile);
  }

  @VisibleForTesting
  static List<WebsiteAssignment> parseWebsiteAssignments(String json) {
    List<WebsiteAssignment> assignments = new ArrayList<>();
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      JsonObject schedule = reader.readObject();
      JsonArray meetings = schedule.getJsonArray("meetings");
      JsonArray lectures = schedule.getJsonArray("lectures");

      if (meetings == null) {
        throw new IllegalArgumentException("Website schedule is missing meetings");
      }

      if (lectures == null) {
        throw new IllegalArgumentException("Website schedule is missing lectures");
      }

      if (lectures.size() > meetings.size()) {
        throw new IllegalArgumentException("Website schedule has more lectures than meetings");
      }

      for (int i = 0; i < lectures.size(); i++) {
        JsonValue lectureValue = lectures.get(i);
        if (lectureValue.getValueType() != JsonValue.ValueType.OBJECT) {
          continue;
        }

        JsonObject lecture = lectureValue.asJsonObject();
        JsonObject topics = lecture.getJsonObject("topics");
        if (topics == null) {
          continue;
        }

        LocalDate dueDate = parseWebsiteDate(meetings.getString(i));
        addDueAssignments(assignments, topics, dueDate);
      }
    }

    return assignments;
  }

  private static LocalDate parseWebsiteDate(String text) {
    return LocalDate.parse(text, WEBSITE_DATE_FORMAT);
  }

  private static void addDueAssignments(List<WebsiteAssignment> assignments, JsonObject topics, LocalDate dueDate) {
    JsonValue due = topics.get("due");
    if (due instanceof JsonString) {
      assignments.add(new WebsiteAssignment(((JsonString) due).getString(), dueDate));

    } else if (due != null && due.getValueType() == JsonValue.ValueType.ARRAY) {
      JsonArray array = topics.getJsonArray("due");
      for (JsonValue value : array) {
        if (value instanceof JsonString) {
          assignments.add(new WebsiteAssignment(((JsonString) value).getString(), dueDate));
        }
      }
    }

    JsonObject quiz = topics.getJsonObject("quiz");
    if (quiz != null && quiz.get("number") instanceof JsonNumber) {
      assignments.add(new WebsiteAssignment("Quiz " + quiz.getInt("number"), dueDate));
    }

    JsonObject survey = topics.getJsonObject("survey");
    if (survey != null && survey.get("name") instanceof JsonString) {
      assignments.add(new WebsiteAssignment(survey.getString("name") + " Survey", dueDate));
    }

    JsonObject reflection = topics.getJsonObject("reflection");
    if (reflection != null && reflection.get("title") instanceof JsonString) {
      assignments.add(new WebsiteAssignment("Reflections on " + reflection.getString("title"), dueDate));
    }
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
          dueDate = Optional.of(OffsetDateTime.parse(((JsonString) dueAt).getString())
            .atZoneSameInstant(PACIFIC_TIME_ZONE)
            .toLocalDate());
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
    err.println("usage: java CompareCanvasAndWebsiteSchedules apiTokenFileName websiteJsonFileName");
    err.println("    apiTokenFileName             File containing the Canvas API token");
    err.println("    websiteJsonFileName          File containing the website schedule JSON");
    err.println();
    err.println("Prints assignment due dates from Canvas and the website schedule JSON");
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

  @VisibleForTesting
  static record WebsiteAssignment(String name, LocalDate dueDate) {
    String dueDateAsText() {
      return this.dueDate.toString();
    }
  }
}
