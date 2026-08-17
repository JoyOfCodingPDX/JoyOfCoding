package edu.pdx.cs.joy.grader.canvas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExportCanvasSurveyResponsesTest {

  @Test
  void exportsAnonymizedResponsesFromClassicQuizStudentAnalysisReport(@TempDir File tempDir)
    throws IOException, InterruptedException {
    List<String> authorizationHeaders = new ArrayList<>();
    List<String> redirectedAuthorizationHeaders = new ArrayList<>();
    HttpServer fileServer = HttpServer.create(new InetSocketAddress(0), 0);
    URI fileServerUri = URI.create("http://localhost:" + fileServer.getAddress().getPort());
    fileServer.createContext("/files/student-analysis.csv", exchange -> {
      try {
        redirectedAuthorizationHeaders.add(exchange.getRequestHeaders().getFirst("Authorization"));
        respond(exchange, 200, """
          name,id,section,section_id,submitted,attempt,100: How well prepared were you for the work in this class?,0.5,101: What should future students know?,0.5,102: May I use your answers to these questions (not your name) todescribe this course in the future?,0.0,n correct,n incorrect,score
          Student One,1,001,10,2026-08-10,1,very good,0.5,Use <generics> & write tests,0.5,Yes,0.0,2,0,1
          Student Two,2,001,10,2026-08-10,1,good,0.5,Another useful response,0.5,Yes,0.0,2,0,1
          Student Three,3,001,10,2026-08-10,1,very good,0.5,This answer must not be included,0.5,No,0.0,1,1,0.5
          """);
      } finally {
        exchange.close();
      }
    });
    fileServer.start();

    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    URI canvasBaseUri = URI.create("http://localhost:" + server.getAddress().getPort());
    server.createContext("/", exchange -> {
      try {
        authorizationHeaders.add(exchange.getRequestHeaders().getFirst("Authorization"));
        respondToCanvasRequest(exchange, canvasBaseUri, fileServerUri);
      } finally {
        exchange.close();
      }
    });
    server.start();

    try {
      File output = new File(tempDir, "survey.html");
      ExportCanvasSurveyResponses exporter = new ExportCanvasSurveyResponses(HttpClient.newHttpClient(), canvasBaseUri);
      exporter.export("canvas-token", 42, output.toPath());

      String html = Files.readString(output.toPath());
      assertThat(html, containsString("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\""));
      assertThat(html, containsString("<title>Previously on The Joy of Coding...</title>"));
      assertThat(html, containsString("<h1>Previously on The Joy of Coding...</h1>"));
      assertThat(html, containsString("<p>Here are some comments from students who have taken The Joy of Coding.</p>"));
      assertThat(html, containsString("<tr><th>Response</th><th>Students</th></tr>"));
      assertThat(html, containsString("<tr><td>very good</td><td>1</td></tr>"));
      assertThat(html, containsString("<tr><td>good</td><td>1</td></tr>"));
      assertThat(html, containsString("<tr><td>fair</td><td>0</td></tr>"));
      assertThat(html, containsString("<tr><td>poor</td><td>0</td></tr>"));
      assertThat(html, containsString("<tr><td>very poor</td><td>0</td></tr>"));
      assertThat(html, containsString("What should future students know?"));
      assertThat(html, containsString("Use &lt;generics&gt; &amp; write tests"));
      assertThat(html, not(containsString("This answer must not be included")));
      assertThat(html, not(containsString("May I use your answers to these questions")));
      assertThat(html, not(containsString("Yes")));
      assertThat(html, not(containsString("Student One")));
      assertThat(html, not(containsString("student.one@example.com")));

      assertEquals(6, authorizationHeaders.size());
      authorizationHeaders.forEach(header -> assertEquals("Bearer canvas-token", header));
      assertEquals(1, redirectedAuthorizationHeaders.size());
      assertEquals(null, redirectedAuthorizationHeaders.get(0));
    } finally {
      server.stop(0);
      fileServer.stop(0);
    }
  }

  private static void respondToCanvasRequest(HttpExchange exchange, URI canvasBaseUri, URI fileServerUri) throws IOException {
    String path = exchange.getRequestURI().getPath();
    if (path.equals("/api/v1/courses/42/quizzes") && "page=2".equals(exchange.getRequestURI().getQuery())) {
      respond(exchange, 200, """
        [{"id": 9, "title": "End of Term Survey", "quiz_type": "assignment"}]
        """);

    } else if (path.equals("/api/v1/courses/42/quizzes")) {
      exchange.getResponseHeaders().add("Link",
        "<" + canvasBaseUri + "/api/v1/courses/42/quizzes?page=2>; rel=\"next\"");
      respond(exchange, 200, """
        [{"id": 8, "title": "Other Quiz", "quiz_type": "assignment"}]
        """);

    } else if (path.equals("/api/v1/courses/42/quizzes/9/reports") && "POST".equals(exchange.getRequestMethod())) {
      String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      assertThat(requestBody, containsString("\"report_type\":\"student_analysis\""));
      assertThat(requestBody, containsString("\"includes_all_versions\":true"));
      respond(exchange, 201, """
        {
          "progress_url": "%s/progress/4",
          "url": "%s/api/v1/courses/42/quizzes/9/reports/7"
        }
        """.formatted(canvasBaseUri, canvasBaseUri));

    } else if (path.equals("/progress/4")) {
      respond(exchange, 200, """
        {"workflow_state": "completed"}
        """);

    } else if (path.equals("/api/v1/courses/42/quizzes/9/reports/7")) {
      respond(exchange, 200, """
        {"file": {"url": "%s/files/student-analysis.csv"}}
        """.formatted(canvasBaseUri));

    } else if (path.equals("/files/student-analysis.csv")) {
      exchange.getResponseHeaders().add("Location", fileServerUri + "/files/student-analysis.csv");
      exchange.sendResponseHeaders(302, -1);

    } else {
      respond(exchange, 404, "{}");
    }
  }

  private static void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, bytes.length);
    exchange.getResponseBody().write(bytes);
  }
}
