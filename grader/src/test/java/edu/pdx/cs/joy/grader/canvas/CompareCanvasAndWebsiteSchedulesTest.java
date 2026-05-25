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
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class CompareCanvasAndWebsiteSchedulesTest {

  @Test
  void printsAssignmentDueDatesFromCanvasAndWebsiteJson(@TempDir File tempDir) throws IOException, InterruptedException {
    File apiTokenFile = writeFile(tempDir, "canvas-token.txt", "canvas-token");
    File websiteJsonFile = writeFile(tempDir, "schedule.json", """
      {
        "meetings" : [
          "June 22, 2026",
          "June 24, 2026",
          "June 29, 2026"
        ],
        "lectures" : [
          {
            "session" : 1,
            "topics" : {
              "projects" : [
                {
                  "title" : "Java Koans"
                }
              ]
            }
          },
          {
            "session" : 2,
            "topics" : {
              "due" : [ "Java Koans" ],
              "quiz" : {
                "number" : 1
              }
            }
          },
          {
            "session" : 3,
            "topics" : {
              "due" : "Project 1",
              "survey" : {
                "name" : "Midterm"
              },
              "reflection" : {
                "title" : "pair programming"
              }
            }
          }
        ]
      }
      """);

    HttpServer server = createCanvasServer((exchange, canvasBaseUri) -> {
      String path = exchange.getRequestURI().getPath();
      if (path.equals("/api/v1/courses/2/assignments")) {
        respond(exchange, 200, """
          [
            {"id": 11, "name": "Quiz 1", "due_at": "2026-06-25T06:59:00Z"},
            {"id": 12, "name": "Project 1", "due_at": "2026-07-01T06:59:00Z"},
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
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      CompareCanvasAndWebsiteSchedules tool = new CompareCanvasAndWebsiteSchedules(HttpClient.newHttpClient(),
        canvasBaseUri(server), fixedClock("2026-05-25T16:00:00Z"));
      tool.run(new String[] { apiTokenFile.getAbsolutePath(), websiteJsonFile.getAbsolutePath() },
        new PrintStream(output, true, StandardCharsets.UTF_8));

      assertThat(output.toString(StandardCharsets.UTF_8), equalTo("""
        Assignments with differing due dates:
        Assignment  Canvas      Website   
        ----------  ----------  ----------
        Project 1   2026-06-30  2026-06-29
        
        Assignments whose due dates couldn't be determined:
        Assignment                       Canvas         Website
        -------------------------------  -------------  -----------
        Java Koans                       (not found)    2026-06-24
        Midterm Survey                   (not found)    2026-06-29
        Reflections on pair programming  (not found)    2026-06-29
        Survey 1                         (no due date)  (not found)
        
        Assignments with matching due dates:
        Assignment  Canvas      Website   
        ----------  ----------  ----------
        Quiz 1      2026-06-24  2026-06-24
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
          "due_at": "2026-06-25T06:59:00Z"
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

  @Test
  void parseWebsiteAssignmentsUsesMeetingDateForDueWork() {
    List<CompareCanvasAndWebsiteSchedules.WebsiteAssignment> assignments =
      CompareCanvasAndWebsiteSchedules.parseWebsiteAssignments("""
        {
          "meetings" : [
            "June 22, 2026",
            "June 24, 2026",
            "June 29, 2026"
          ],
          "lectures" : [
            {
              "session" : 1,
              "topics" : {
                "projects" : [
                  {
                    "title" : "Java Koans"
                  }
                ]
              }
            },
            {
              "session" : 2,
              "topics" : {
                "due" : [ "Java Koans" ],
                "quiz" : {
                  "number" : 1
                }
              }
            },
            {
              "session" : 3,
              "topics" : {
                "due" : "Project 1",
                "survey" : {
                  "name" : "Midterm"
                },
                "reflection" : {
                  "title" : "pair programming"
                }
              }
            }
          ]
        }
        """);

    assertThat(assignments, contains(
      new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Java Koans", LocalDate.parse("2026-06-24")),
      new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Quiz 1", LocalDate.parse("2026-06-24")),
      new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Project 1", LocalDate.parse("2026-06-29")),
      new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Midterm Survey", LocalDate.parse("2026-06-29")),
      new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Reflections on pair programming", LocalDate.parse("2026-06-29"))));
  }

  @Test
  void compareAssignmentsCategorizesDifferencesUnknownsAndMatches() {
    CompareCanvasAndWebsiteSchedules.ComparisonReport report = CompareCanvasAndWebsiteSchedules.compareAssignments(
      List.of(
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Quiz 1", Optional.of(LocalDate.parse("2026-06-24"))),
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Project 1", Optional.of(LocalDate.parse("2026-06-30"))),
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Survey 1", Optional.empty())
      ),
      List.of(
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Java Koans", LocalDate.parse("2026-06-24")),
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Quiz 1", LocalDate.parse("2026-06-24")),
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Project 1", LocalDate.parse("2026-06-29")),
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Midterm Survey", LocalDate.parse("2026-06-29"))
      ));

    assertThat(report.differingDueDates(), contains(
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Project 1", "2026-06-30", "2026-06-29")));
    assertThat(report.undeterminedDueDates(), contains(
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Java Koans", "(not found)", "2026-06-24"),
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Midterm Survey", "(not found)", "2026-06-29"),
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Survey 1", "(no due date)", "(not found)")));
    assertThat(report.matchingDueDates(), contains(
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Quiz 1", "2026-06-24", "2026-06-24")));
  }

  @Test
  void compareAssignmentsMatchesCaseInsensitiveQuizPrefixesAndReflectionNames() {
    CompareCanvasAndWebsiteSchedules.ComparisonReport report = CompareCanvasAndWebsiteSchedules.compareAssignments(
      List.of(
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Quiz 2: Java Language and OOP",
          Optional.of(LocalDate.parse("2026-07-08"))),
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("End of Term Survey",
          Optional.of(LocalDate.parse("2026-08-12"))),
        new CompareCanvasAndWebsiteSchedules.CanvasAssignment("Reflections on Pair Programming",
          Optional.of(LocalDate.parse("2026-07-22")))
      ),
      List.of(
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Quiz 2", LocalDate.parse("2026-07-08")),
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("End of term Survey", LocalDate.parse("2026-08-12")),
        new CompareCanvasAndWebsiteSchedules.WebsiteAssignment("Reflections on your experiences pair programming",
          LocalDate.parse("2026-07-22"))
      ));

    assertThat(report.differingDueDates(), equalTo(List.of()));
    assertThat(report.undeterminedDueDates(), equalTo(List.of()));
    assertThat(report.matchingDueDates(), contains(
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("End of term Survey", "2026-08-12", "2026-08-12"),
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Quiz 2", "2026-07-08", "2026-07-08"),
      new CompareCanvasAndWebsiteSchedules.ComparisonRow("Reflections on your experiences pair programming",
        "2026-07-22", "2026-07-22")));
  }

  private static File writeFile(File tempDir, String fileName, String content) throws IOException {
    File file = new File(tempDir, fileName);
    Files.writeString(file.toPath(), content);
    return file;
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

  private static void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
    byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(statusCode, bytes.length);
    exchange.getResponseBody().write(bytes);
  }

  private static URI canvasBaseUri(HttpServer server) {
    return URI.create("http://localhost:" + server.getAddress().getPort());
  }

  private static java.time.Clock fixedClock(String instant) {
    return java.time.Clock.fixed(Instant.parse(instant), java.time.ZoneOffset.UTC);
  }

  @FunctionalInterface
  private interface CanvasHandler {
    void handle(HttpExchange exchange, URI canvasBaseUri) throws IOException;
  }
}
