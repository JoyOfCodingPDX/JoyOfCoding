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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class CompareCanvasAndWebsiteSchedulesTest {

  @Test
  void printsCourseNamesReturnedByCanvas(@TempDir File tempDir) throws IOException, InterruptedException {
    AtomicReference<String> authorizationHeader = new AtomicReference<>();
    HttpServer server = createCanvasServer(exchange -> {
      authorizationHeader.set(exchange.getRequestHeaders().getFirst("Authorization"));
      respond(exchange, 200, """
        [
          {"id": 1, "name": "Java Koans"},
          {"id": 2, "name": "Project 1"}
        ]
        """);
    });
    try {
      File apiTokenFile = writeApiTokenFile(tempDir, "canvas-token");
      URI canvasBaseUri = canvasBaseUri(server);

      ByteArrayOutputStream output = new ByteArrayOutputStream();
      CompareCanvasAndWebsiteSchedules tool = new CompareCanvasAndWebsiteSchedules(HttpClient.newHttpClient(), canvasBaseUri);
      tool.run(new String[] { apiTokenFile.getAbsolutePath() }, new PrintStream(output, true, StandardCharsets.UTF_8));

      assertThat(output.toString(StandardCharsets.UTF_8), equalTo("Java Koans%nProject 1%n".formatted()));
      assertThat(authorizationHeader.get(), equalTo("Bearer canvas-token"));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void printsCourseNamesFromAllCanvasPages(@TempDir File tempDir) throws IOException, InterruptedException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    URI canvasBaseUri = canvasBaseUri(server);
    server.createContext("/api/v1/courses", exchange -> {
      String query = exchange.getRequestURI().getQuery();
      try {
        if ("page=2".equals(query)) {
          respond(exchange, 200, """
            [
              {"id": 2, "name": "Project 2"}
            ]
            """);

        } else {
          exchange.getResponseHeaders().add("Link", "<%s/api/v1/courses?page=2>; rel=\"next\"".formatted(canvasBaseUri));
          respond(exchange, 200, """
            [
              {"id": 1, "name": "Project 1"}
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
      CompareCanvasAndWebsiteSchedules tool = new CompareCanvasAndWebsiteSchedules(HttpClient.newHttpClient(), canvasBaseUri);
      tool.run(new String[] { apiTokenFile.getAbsolutePath() }, new PrintStream(output, true, StandardCharsets.UTF_8));

      assertThat(output.toString(StandardCharsets.UTF_8), equalTo("Project 1%nProject 2%n".formatted()));
    } finally {
      server.stop(0);
    }
  }

  @Test
  void parseCourseNamesIgnoresNestedNameFields() {
    List<String> courseNames = CompareCanvasAndWebsiteSchedules.parseCourseNames("""
      [
        {
          "id": 1,
          "name": "Java Koans",
          "term": {
            "name": "Spring 2026"
          }
        },
        {
          "id": 2,
          "course_code": "CS 410",
          "name": "Project 1"
        }
      ]
      """);

    assertThat(courseNames, contains("Java Koans", "Project 1"));
  }

  private static HttpServer createCanvasServer(CanvasHandler handler) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/api/v1/courses", exchange -> {
      try {
        handler.handle(exchange);
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

  @FunctionalInterface
  private interface CanvasHandler {
    void handle(HttpExchange exchange) throws IOException;
  }
}
