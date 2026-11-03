# JoyOfCoding repository instructions

## Build, test, and lint

- Use the Maven Wrapper from the repository root. The CI build is `./mvnw --batch-mode verify --file pom.xml`; it runs unit tests, integration tests, and the configured JaCoCo reporting. CI runs this with JDK 21 and 24. The reactor enforces Java 21, except `projects-parent/projects`, which deliberately compiles Java 8-compatible APIs.
- Run a module's unit tests with its required reactor dependencies: `./mvnw --batch-mode -pl <module> -am test`. For one test or method, add `-Dtest=FullyQualifiedTestClass` or `-Dtest=FullyQualifiedTestClass#method`; for example, `./mvnw --batch-mode -pl projects-parent/projects -am -Dtest=edu.pdx.cs.joy.InvokeMainTest test`.
- Integration tests live in `src/it/java`, are compiled separately, and run through Failsafe during `verify`. Run one with `./mvnw --batch-mode -pl web -am -Dit.test=edu.pdx.cs.joy.servlets.MovieDatabaseServletIT verify`. The `web` module starts Jetty on port 8080 for its integration-test lifecycle.
- The project does not have one reactor-wide lint phase. The reference project modules under `projects-parent/originals-parent` bind Checkstyle; run it for a changed reference project with `./mvnw --batch-mode -pl projects-parent/originals-parent/<project> checkstyle:check`. Generate the Maven documentation site with `./mvnw --batch-mode site --file pom.xml`.

## Architecture

- This is a Maven multi-module codebase for "The Joy of Coding" course. The root reactor builds `examples`, `web`, `family`, `grader`, and `projects-parent`.
- `projects-parent/projects` is the shared course API: abstract domain types, parsers/dumpers, XML helpers, and test utilities used by the course projects. Keep it Java 8-compatible because Android projects consume it.
- `projects-parent/originals-parent` contains the reference implementations of student assignments (airline, appointment book, phone bill, student, and kata, including web variants). `projects-parent/archetypes-parent` packages the corresponding student project templates. Template source is under each archetype's `src/main/resources/archetype-resources`; its `META-INF/maven/archetype-metadata.xml` controls which files are filtered and packaged.
- `examples` contains independent Java examples, while `family` is a family-tree application and `grader` is the course grading/gradebook tooling. The grader produces a shaded executable with `edu.pdx.cs.joy.grader.GraderTools` as its entry point.
- `web` packages a WAR. Traditional servlet endpoints and URL mappings are declared in `web/src/main/webapp/WEB-INF/web.xml`; RESTEasy/Guice REST services are registered in `edu.pdx.cs.joy.di.RestModule` and served beneath `/rest`.

## Codebase conventions

- Use JUnit Jupiter for tests, with Hamcrest matchers commonly used for expressive assertions. Keep fast tests in `src/test/java`; put tests that need external services, Jetty, or mail fixtures in `src/it/java` and name them `*IT`.
- Course-project command-line tests should extend `edu.pdx.cs.joy.InvokeMainTestCase` from the shared `projects` test JAR. Its default invocation rejects mutable static fields and captures standard output/error; use its explicit `invokeMainAllowingMutableStaticFields` only when that behavior is intentional.
- Reference projects are built with the `grader` profile, which applies API-doclet validation and JaCoCo thresholds (at least 75% instruction coverage and no missed classes). Preserve the original project and archetype-template distinction when changing assignment-facing behavior.
