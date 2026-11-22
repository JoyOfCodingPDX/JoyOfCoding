---
description: 'Compare the next Portland State Canvas course offering assignment due dates with the authoritative Joy of Coding website schedule. Use when asked to audit Canvas dates, quizzes, projects, or course schedule dates.'
---

# Compare Canvas and website schedule

Use the existing `edu.pdx.cs.joy.grader.canvas.CompareCanvasAndWebsiteSchedules`
tool. This skill is read-only: it must not modify Canvas, the schedule JSON, or
any gradebook.

## Inputs

1. Locate the Canvas API token file supplied by the instructor. Never print its
   contents, include it in a report, or commit it.
2. Locate the authoritative schedule JSON in the sibling
   `PortlandStateWebsite/js` checkout. Prefer the term explicitly requested by
   the user, for example `schedule-winter2027.json`.
3. Run from the JoyOfCoding repository root. If the grader JAR is absent or
   stale, build it first with:

   ```sh
   ./mvnw --batch-mode -pl grader -am package -DskipTests
   ```

## Run the comparison

Invoke the tool with the token path and schedule path:

```sh
java -cp grader/target/grader-*-SNAPSHOT.jar \
  edu.pdx.cs.joy.grader.canvas.CompareCanvasAndWebsiteSchedules \
  /path/to/canvas-token.txt \
  /path/to/PortlandStateWebsite/js/schedule-winter2027.json
```

Use a specific matching grader JAR if more than one exists; do not pass a
sources, javadoc, or original JAR. The tool discovers the earliest Canvas
course whose `start_at` is after the current time, then compares that course's
assignments with the supplied schedule. Confirm that the selected upcoming
course is the requested term before treating the output as authoritative.

## Report

Present the tool output without exposing credentials. Clearly call out:

- assignments with differing due dates;
- assignments whose dates could not be determined, including missing Canvas
  assignments and assignments without a due date; and
- assignments with matching dates.

Explain that website dates are derived from the meeting date associated with
each scheduled `due`, quiz, survey, reflection, POA, or final-exam entry, and
that Canvas timestamps are rendered as Pacific calendar dates by the tool.
Do not silently treat an unknown assignment or missing due date as a match.
