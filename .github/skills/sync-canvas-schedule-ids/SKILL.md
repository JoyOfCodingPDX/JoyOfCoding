---
description: 'Synchronize canvasCourseId and Canvas quiz IDs in a requested Joy of Coding schedule JSON file. Use only when explicitly asked to update schedule IDs from Canvas.'
---

# Synchronize Canvas schedule IDs

This skill updates only the requested `PortlandStateWebsite/js/schedule-*.json`
file. It must not create, edit, or delete Canvas assignments or quizzes.

## Safety requirements

1. Require an explicit term and schedule path from the user; never infer the
   target from the current date.
2. Locate the instructor's Canvas API token without printing its contents or
   committing it.
3. Confirm the Canvas course by exact term/course identity and start date.
   Never use the generic "next course" selection for a write operation.
4. Fetch Canvas assignments and quizzes with pagination. Match schedule entries
   by normalized title:
   - `Quiz N` matches the Canvas assignment whose title starts with `Quiz N`;
   - `Reflections on {title}` matches the corresponding Canvas assignment;
   - `{name} Survey` matches the corresponding Canvas quiz title.
   Store assignment IDs for numbered quizzes and reflections, and quiz IDs for
   surveys, because the website links these through different Canvas URL types.
5. Refuse to write if the course is ambiguous, an expected quiz is missing,
   duplicate titles exist, or an API request fails.
6. Show a complete dry-run mapping and `git diff` before writing. Write only
   after the user explicitly confirms the proposed changes.
7. Preserve the schedule's existing formatting and all unrelated content.

## Procedure

From the JoyOfCoding repository root:

1. Read the requested schedule and enumerate every `quizId` location, including
   surveys and reflections. Confirm that the schedule's `canvasCourseId` and
   all listed IDs are currently placeholders or match the intended course.
2. Query Canvas:

   ```text
   GET /api/v1/courses?per_page=100
   GET /api/v1/courses/{courseId}/assignments?per_page=100
   GET /api/v1/courses/{courseId}/quizzes?per_page=100
   ```

   Follow each response's `Link: ... rel="next"` pagination link. Retain only
   the numeric `id`, `name`, and course identity needed for the report.
3. Build the proposed mapping from each schedule title to exactly one Canvas
   assignment or quiz ID, according to the URL type above. Do not assume IDs
   are sequential or equal to quiz numbers.
4. Present the course identity, every title-to-ID mapping, and the proposed
   file changes as a dry run. Stop if anything is unresolved.
5. After confirmation, update only the targeted scalar values using a
   formatting-preserving edit. Do not reserialize the complete JSON document.
6. Validate JSON syntax, verify that the course ID and every expected quiz ID
   occur in the intended locations, and show `git diff --check` plus the final
   `git diff`.

## Completion report

Report the selected Canvas course, every updated schedule field, validation
results, and any unresolved items. Never include the API token or raw
authorization headers.
