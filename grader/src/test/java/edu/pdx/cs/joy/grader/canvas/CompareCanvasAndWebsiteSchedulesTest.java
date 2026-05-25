package edu.pdx.cs.joy.grader.canvas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class CompareCanvasAndWebsiteSchedulesTest {

  @Test
  void printsAssignmentDueDatesForNextCourseOffering(@TempDir File tempDir) throws IOException, InterruptedException {
    AtomicReference<String> authorizationHeader = new AtomicReference<>();
    HttpServer server = createCanvasServer((exchange, canvasBaseUri) -> {
      authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
      if (exchange.getRequestURI().getPath().equals("/api/v1/courses/2/assignments")) {
        respond(exchange, 200, """
          [
            {"id": 11, "name": "Quiz 1", "due_at": "2026-06-24T23:59:00Z"},
            {"id": 12, "name": "Project 1", "due_at": "2026-06-29T23:59:00Z"},
            {"id": 13, "name": "Survey 1", "due_at": null}
          ]
          """);

      } else {
        respond(exchange, 200, """
          [
            {"id": 1, "name": "Spring 2026", "start_at": "2026-03-30T16:00:00Z"},
            {"id": 2, "name": "Summer 2026", "start_at": "2026-06-22T16:00:00Z"},
            {"id": 3, "name": "Fall 2026", "start_at": "2026-09-28T16:00:00Z"}
          ]
          """);
      }
    });
    try {
      File apiTokenFile = writeApiTokenFile(tempDir, "canvas-token");
      URI canvasBaseUri = canvasBaseUri(server);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      CompareCanvasAndWebsiteSchedules tool = new CompareCanvasAndWebsiteSchedules(HttpClient.newHttpClient(), canvasBaseUri,
        fixedClock("2026-05-25T16:00:00Z"));
      tool.run(new String[] { apiTokenFile.getAbsolutePath() }, new PrintStream(output, true, StandardCharsets.UTF_8));

      assertThat(output.toString(StandardCharsets.UTF_8), equalTo("""
        Quiz 1: 2026-06-24
        Project 1: 2026-06-29
        Survey 1: (no due date)
        """));
      assertThat(authorizationHeader.get(), equalTo("Bearer canvas-token"));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void printsAssignmentsFromAllCanvasPagesForNextCourseOffering(@TempDir File tempDir) throws IOException, InterruptedException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    URI canvasBaseUri = canvasBaseUri(server);
    server.createContext("/api/v1/courses", exchange -> {
      String query = exchange.getRequestURI().getQuery();
      try {
        if ("page=2".equals(query)) {
          respond(exchange, 200, """
            [
              {"id": 3, "name": "Fall 2026", "start_at": "2026-09-28T16:00:00Z"},
              {"id": 4, "name": "Winter 2027", "start_at": "2027-01-04T16:00:00Z"}
            ]
            """);

        } else {
          exchange.getResponseHeaders().add("Link", "<%s/api/v1/courses?page=2>; rel=\"next\"".formatted(canvasBaseUri));
          respond(exchange, 200, """
            [
              {"id": 1, "name": "Spring 2026", "start_at": "2026-03-30T16:00:00Z"},
              {"id": 2, "name": "Summer 2026", "start_at": "2026-06-22T16:00:00Z"}
            ]
            """);
        }
      } finally {
        exchange.close();
      }
    });
    server.createContext("/api/v1/courses/2/assignments", exchange -> {
      String query = exchange.getRequestURI().getQuery();
      try {
        if ("page=2".equals(query)) {
          respond(exchange, 200, """
            [
              {"id": 13, "name": "POA 1", "due_at": "2026-06-23T23:59:00Z"}
            ]
            """);

        } else {
          exchange.getResponseHeaders().add("Link", "<%s/api/v1/courses/2/assignments?page=2>; rel=\"next\"".formatted(canvasBaseUri));
          respond(exchange, 200, """
            [
              {"id": 11, "name": "Quiz 1", "due_at": "2026-06-24T23:59:00Z"},
              {"id": 12, "name": "Project 1", "due_at": "2026-06-29T23:59:00Z"}
            ]
            """);
        }
      } finally {
        exchange.close();
      }
    });
    server.start();
    try {
      File apiTokenFile = writeApiTokenFile(tempDir, "canvas-token");

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      CompareCanvasAndWebsiteSchedules tool = new CompareCanvasAndWebsiteSchedules(HttpClient.newHttpClient(), canvasBaseUri,
        fixedClock("2026-05-25T16:00:00Z"));
      tool.run(new String[] { apiTokenFile.getAbsolutePath() }, new PrintStream(output, true, StandardCharsets.UTF_8));

      assertThat(output.toString(StandardCharsets.UTF_8), equalTo("""
        Quiz 1: 2026-06-24
        Project 1: 2026-06-29
        POA 1: 2026-06-23
        """));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void parseCoursesIgnoresCoursesWithoutTopLevelStartDate() {
    List<CompareCanvasAndWebsiteSchedules.CanvasCourse> courses = CompareCanvasAndWebsiteSchedules.parseCourses("""
      [
        {
          "id": 1,
          "name": "Java Koans",
          "start_at": "2026-06-22T16:00:00Z",
          "term": {
            "name": "Spring 2026"
          }
        },
        {
          "id": 2,
          "course_code": "CS 410",
          "name": "Project 1"
        },
        {
          "id": 3,
          "name": "Project 2",
          "start_at": null
        }
      ]
      """);

    assertThat(courses, contains(
      new CompareCanvasAndWebsiteSchedules.CanvasCourse(1, "Java Koans", Instant.parse("2026-06-22T16:00:00Z"))));
  }

  @Test
  void parseAssignmentsIncludesAssignmentsWithoutDueDates() {
    List<CompareCanvasAndWebsiteSchedules.CanvasAssignment> assignments = CompareCanvasAndWebsiteSchedules.parseAssignments("""
      [
        {
          "id": 11,
          "name": "Quiz 1",
          "due_at": "2026-06-24T23:59:00Z"
        },
        {
          "id": 12,
          "name": "Survey 1",
          "due_at": null
        },
        {
          "id": 13,
          "due_at": "2026-06-25T23:59:00Z"
        }
      ]
      """);

    assertThat(assignments, contains(
      new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Quiz 1", Optional.of(LocalDate.parse("2026-06-24"))),
      new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Survey 1", Optional.empty())));
  }

  private static HttpServer createCanvasServer(CanvasHandler handler) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    URI canvasBaseUri = canvasBaseUri(server);
    server.createContext("/api/v1/courses", exchange -> {
      try {
        handler.handle(exchange, canvasBaseUri);
      } finally {
        exchange.close();
      }
    });
    server.createContext("/api/v1/courses/2/assignments", exchange -> {
      try {
        handler.handle(exchange, canvasBaseUri);
      } finally {
        exchange.close();
      }
    });
    server.start();
    return server;
  }

  private static File writeApiTokenFile(File tempDir, String token) throws IOException {
    File apiTokenFile = new File(tempDir, "canvas-token.txt");
    Files.writeString(apiTokenFile.toPath(), token);
    return apiTokenFile;
  }

  private static void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, bytes.length);
    exchange.getResponseBody().write(bytes);
  }

  private static URI canvasBaseUri(HttpServer server) {
    return URI.create("http://localhost:" + server.getAddress().getPort());
  }

  private static Clock fixedClock(String instant) {
    return Clock.fixed(Instant.parse(instant), ZoneOffset.UTC);
  }

  @FunctionalInterface
  private interface CanvasHandler {
    void handle(HttpExchange exchange, URI canvasBaseUri) throws IOException;
  }
}
