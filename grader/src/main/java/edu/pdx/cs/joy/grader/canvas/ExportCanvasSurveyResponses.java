package edu.pdx.cs.joy.grader.canvas;

import com.google.common.annotations.VisibleForTesting;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exports the responses to the End of Term Survey from a Canvas Classic Quiz
 * as anonymized HTML.
 */
public class ExportCanvasSurveyResponses {
  static final URI DEFAULT_CANVAS_BASE_URI = URI.create("https://canvas.pdx.edu");
  static final String SURVEY_TITLE = "End of Term Survey";

  private static final Pattern NEXT_LINK_PATTERN = Pattern.compile("<([^>]+)>;\\s*rel=\"next\"");
  private static final Pattern QUESTION_COLUMN_PATTERN = Pattern.compile("^\\d+:\\s+(.+)$");
  private static final String CONSENT_QUESTION =
    "May I use your answers to these questions (not your name) todescribe this course in the future?";
  private static final Duration REPORT_POLL_DELAY = Duration.ofSeconds(1);

  private final HttpClient httpClient;
  private final URI canvasBaseUri;

  public ExportCanvasSurveyResponses() {
    this(HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build(), DEFAULT_CANVAS_BASE_URI);
  }

  @VisibleForTesting
  ExportCanvasSurveyResponses(HttpClient httpClient, URI canvasBaseUri) {
    this.httpClient = httpClient;
    this.canvasBaseUri = canvasBaseUri;
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    try {
      new ExportCanvasSurveyResponses().run(args);

    } catch (IllegalArgumentException | IllegalStateException ex) {
      usage(ex.getMessage());
    }
  }

  @VisibleForTesting
  void run(String[] args) throws IOException, InterruptedException {
    if (args.length == 0) {
      throw new IllegalArgumentException("Missing Canvas API token file name");
    }
    if (args.length == 1) {
      throw new IllegalArgumentException("Missing Canvas course ID");
    }
    if (args.length == 2) {
      throw new IllegalArgumentException("Missing HTML output file name");
    }
    if (args.length > 3) {
      throw new IllegalArgumentException("Extraneous command line argument: " + args[3]);
    }

    String apiToken = readApiToken(Path.of(args[0]));
    int courseId = parseCourseId(args[1]);
    Path outputFile = Path.of(args[2]);

    export(apiToken, courseId, outputFile);
  }

  @VisibleForTesting
  void export(String apiToken, int courseId, Path outputFile) throws IOException, InterruptedException {
    CanvasQuiz quiz = findClassicSurvey(apiToken, courseId);
    String reportCsv = downloadStudentAnalysisReport(apiToken, courseId, quiz.id());
    Map<String, List<String>> responses = parseSurveyResponses(reportCsv);
    writeHtml(outputFile, responses);
  }

  private CanvasQuiz findClassicSurvey(String apiToken, int courseId) throws IOException, InterruptedException {
    List<CanvasQuiz> matches = new ArrayList<>();
    URI nextPage = this.canvasBaseUri.resolve("/api/v1/courses/" + courseId + "/quizzes?per_page=100");

    while (nextPage != null) {
      HttpResponse<String> response = invokeCanvas(nextPage, apiToken, HttpRequest.BodyPublishers.noBody());
      for (CanvasQuiz quiz : parseQuizzes(response.body())) {
        if (SURVEY_TITLE.equals(quiz.title())) {
          matches.add(quiz);
        }
      }
      nextPage = getNextPage(response.headers());
    }

    if (matches.isEmpty()) {
      throw new IllegalStateException("Canvas course " + courseId + " has no quiz named \"" + SURVEY_TITLE + "\"");
    }
    if (matches.size() > 1) {
      throw new IllegalStateException("Canvas course " + courseId + " has multiple quizzes named \"" + SURVEY_TITLE + "\"");
    }

    CanvasQuiz survey = matches.get(0);
    if (survey.quizType() == null) {
      throw new IllegalStateException("\"" + SURVEY_TITLE + "\" is not a Classic Quiz");
    }
    return survey;
  }

  private String downloadStudentAnalysisReport(String apiToken, int courseId, int quizId) throws IOException, InterruptedException {
    URI reportsUri = this.canvasBaseUri.resolve("/api/v1/courses/" + courseId + "/quizzes/" + quizId + "/reports");
    String body = """
      {"quiz_report":{"report_type":"student_analysis","includes_all_versions":true}}
      """;
    HttpResponse<String> response = invokeCanvas(reportsUri, apiToken, HttpRequest.BodyPublishers.ofString(body));
    JsonObject report = parseObject(response.body());

    JsonString progressUrl = report.getJsonString("progress_url");
    JsonString reportUrl = report.getJsonString("url");
    if (progressUrl == null || reportUrl == null) {
      throw new IOException("Canvas did not return a report progress URL");
    }

    waitForReport(apiToken, URI.create(progressUrl.getString()));
    JsonObject completedReport = getJsonObject(apiToken, URI.create(reportUrl.getString()));
    JsonObject file = completedReport.getJsonObject("file");
    if (file == null || file.getJsonString("url") == null) {
      throw new IOException("Canvas did not return a generated report file");
    }

    return downloadReportFile(apiToken, URI.create(file.getString("url")));
  }

  private String downloadReportFile(String apiToken, URI fileUrl) throws IOException, InterruptedException {
    URI currentUrl = fileUrl;
    boolean includeAuthorization = true;

    while (true) {
      HttpResponse<String> response = sendRequest(currentUrl, apiToken, HttpRequest.BodyPublishers.noBody(), includeAuthorization);
      if (response.statusCode() >= 200 && response.statusCode() < 300) {
        return response.body();
      }
      if (response.statusCode() < 300 || response.statusCode() >= 400) {
        throw new IOException("Canvas report download from " + currentUrl + " failed with status code " + response.statusCode());
      }

      String location = response.headers().firstValue("Location")
        .orElseThrow(() -> new IOException("Canvas report download redirected without a Location header"));
      currentUrl = currentUrl.resolve(location);
      includeAuthorization = isCanvasUri(currentUrl);
    }
  }

  private void waitForReport(String apiToken, URI progressUrl) throws IOException, InterruptedException {
    while (true) {
      JsonObject progress = getJsonObject(apiToken, progressUrl);
      String workflowState = progress.getString("workflow_state", "");
      if ("completed".equals(workflowState)) {
        return;
      }
      if ("failed".equals(workflowState)) {
        throw new IOException("Canvas failed to generate the student analysis report");
      }

      Thread.sleep(REPORT_POLL_DELAY);
    }
  }

  private JsonObject getJsonObject(String apiToken, URI uri) throws IOException, InterruptedException {
    HttpResponse<String> response = invokeCanvas(uri, apiToken, HttpRequest.BodyPublishers.noBody());
    return parseObject(response.body());
  }

  private HttpResponse<String> invokeCanvas(URI uri, String apiToken, HttpRequest.BodyPublisher body)
    throws IOException, InterruptedException {
    if (!isCanvasUri(uri)) {
      throw new IllegalArgumentException("Canvas API returned an unexpected URI: " + uri);
    }

    HttpResponse<String> response = sendRequest(uri, apiToken, body, true);
    if (response.statusCode() < 200 || response.statusCode() >= 300) {
      throw new IOException("Canvas request to " + uri + " failed with status code " + response.statusCode());
    }
    return response;
  }

  private HttpResponse<String> sendRequest(URI uri, String apiToken, HttpRequest.BodyPublisher body, boolean includeAuthorization)
    throws IOException, InterruptedException {
    HttpRequest.Builder request = HttpRequest.newBuilder(uri)
      .header("Accept", "application/json");
    if (includeAuthorization) {
      request.header("Authorization", "Bearer " + apiToken);
    }
    if (body.contentLength() == 0) {
      request.GET();
    } else {
      request.header("Content-Type", "application/json").POST(body);
    }

    HttpResponse<String> response = this.httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    return response;
  }

  private boolean isCanvasUri(URI uri) {
    return this.canvasBaseUri.getScheme().equalsIgnoreCase(uri.getScheme())
      && this.canvasBaseUri.getHost().equalsIgnoreCase(uri.getHost())
      && this.canvasBaseUri.getPort() == uri.getPort();
  }

  @VisibleForTesting
  static Map<String, List<String>> parseSurveyResponses(String csv) throws IOException {
    try (CSVReader reader = new CSVReader(new StringReader(csv))) {
      String[] header = reader.readNext();
      if (header == null) {
        throw new IOException("Canvas student analysis report is empty");
      }

      Map<Integer, String> questionColumns = findQuestionColumns(header);
      Map<String, List<String>> responses = new LinkedHashMap<>();
      questionColumns.values().forEach(question -> responses.putIfAbsent(question, new ArrayList<>()));

      String[] row;
      while ((row = reader.readNext()) != null) {
        for (Map.Entry<Integer, String> questionColumn : questionColumns.entrySet()) {
          int column = questionColumn.getKey();
          if (column < row.length && !row[column].isBlank()) {
            responses.get(questionColumn.getValue()).add(row[column]);
          }
        }
      }
      return responses;

    } catch (CsvValidationException ex) {
      throw new IOException("While parsing the Canvas student analysis report", ex);
    }
  }

  private static Map<Integer, String> findQuestionColumns(String[] header) {
    Map<Integer, String> questionColumns = new LinkedHashMap<>();
    for (int column = 0; column < header.length; column++) {
      Matcher matcher = QUESTION_COLUMN_PATTERN.matcher(header[column]);
      if (matcher.matches() && !CONSENT_QUESTION.equals(matcher.group(1))) {
        questionColumns.put(column, matcher.group(1));
      }
    }
    if (questionColumns.isEmpty()) {
      throw new IllegalArgumentException("Canvas student analysis report has no question columns");
    }
    return questionColumns;
  }

  private static List<CanvasQuiz> parseQuizzes(String json) {
    List<CanvasQuiz> quizzes = new ArrayList<>();
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      JsonArray values = reader.readArray();
      for (JsonValue value : values) {
        if (value.getValueType() != JsonValue.ValueType.OBJECT) {
          continue;
        }
        JsonObject quiz = value.asJsonObject();
        JsonString title = quiz.getJsonString("title");
        if (title != null && quiz.containsKey("id")) {
          quizzes.add(new CanvasQuiz(quiz.getInt("id"), title.getString(), quiz.getString("quiz_type", null)));
        }
      }
    }
    return quizzes;
  }

  private static JsonObject parseObject(String json) {
    try (JsonReader reader = Json.createReader(new StringReader(json))) {
      return reader.readObject();
    }
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

  private static String readApiToken(Path apiTokenFile) throws IOException {
    if (!Files.exists(apiTokenFile)) {
      throw new IllegalArgumentException("Canvas API token file \"" + apiTokenFile + "\" does not exist");
    }

    String apiToken = Files.readString(apiTokenFile).trim();
    if (apiToken.isEmpty()) {
      throw new IllegalArgumentException("Canvas API token file \"" + apiTokenFile + "\" is empty");
    }
    return apiToken;
  }

  private static int parseCourseId(String courseIdText) {
    try {
      return Integer.parseInt(courseIdText);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Canvas course ID \"" + courseIdText + "\" is not an integer");
    }
  }

  private static void writeHtml(Path outputFile, Map<String, List<String>> responses) throws IOException {
    Path parent = outputFile.toAbsolutePath().getParent();
    if (parent != null && !Files.exists(parent)) {
      throw new IllegalArgumentException("Parent directory \"" + parent + "\" does not exist");
    }

    try (Writer writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
      writer.write("<!doctype html>\n<html>\n<head>\n<meta charset=\"utf-8\">\n<title>");
      writeEscapedHtml(writer, SURVEY_TITLE);
      writer.write("</title>\n</head>\n<body>\n<h1>");
      writeEscapedHtml(writer, SURVEY_TITLE);
      writer.write("</h1>\n<ol>\n");

      for (Map.Entry<String, List<String>> question : responses.entrySet()) {
        writer.write("<li>");
        writeEscapedHtml(writer, question.getKey());
        writer.write("\n<ul>\n");
        for (String answer : question.getValue()) {
          writer.write("<li>");
          writeEscapedHtml(writer, answer);
          writer.write("</li>\n");
        }
        writer.write("</ul>\n</li>\n");
      }
      writer.write("</ol>\n</body>\n</html>\n");
    }
  }

  private static void writeEscapedHtml(Writer writer, String text) throws IOException {
    for (int i = 0; i < text.length(); i++) {
      switch (text.charAt(i)) {
        case '&' -> writer.write("&amp;");
        case '<' -> writer.write("&lt;");
        case '>' -> writer.write("&gt;");
        case '"' -> writer.write("&quot;");
        case '\'' -> writer.write("&#39;");
        default -> writer.write(text.charAt(i));
      }
    }
  }

  private static void usage(String message) {
    PrintStream err = System.err;
    err.println("+++ " + message);
    err.println();
    err.println("usage: java ExportCanvasSurveyResponses apiTokenFileName courseId htmlFileName");
    err.println("    apiTokenFileName  File containing the Canvas API token");
    err.println("    courseId          Canvas ID of the course offering");
    err.println("    htmlFileName      Output file for anonymized survey responses");
    err.println();
    err.println("Exports the \"" + SURVEY_TITLE + "\" Classic Quiz from Canvas as anonymized HTML");
    err.println();
    System.exit(1);
  }

  private record CanvasQuiz(int id, String title, String quizType) {
  }
}
