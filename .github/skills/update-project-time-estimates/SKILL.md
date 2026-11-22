---
description: 'Recalculate the GettingStarted README project-time-estimate table from student grade books beginning with Winter 2022. Use when refreshing the course workload estimates.'
---

# Update project time estimates

This skill regenerates the project-time-estimate Markdown table in
`GettingStarted/README.md` from one aggregate gradebook XML file per course
term. It must not inspect, copy, or modify student project source code.

## Select the input grade books

Ask the user for the local directory containing the gradebook XML files. Store
the answer as `GRADES_ROOT`; do not assume or reveal a machine-specific path.
Also ask for the target offering, such as `winter2027`; include data from
Winter 2022 through the term immediately before that offering, never from the
target or a later offering.

Include the aggregate gradebook file from every course-term directory beginning
with `winter2022` and earlier than the target offering, including Summer
offerings after Winter 2022.
Each aggregate file is named after its directory, such as
`summer2022/summer2022.xml`; the parser loads that term's student XML files
from the same directory.
Exclude:

- individual student XML files;
- editor backups such as `*.xml.~*`;
- files under `final/` or other nested directories; and
- non-XML files.

The aggregate gradebooks contain the student submission estimates consumed by
`ProjectTimeEstimatesSummary`. Do not pass individual student XML files or
Canvas CSV files as inputs.

## Generate the table

Run from the JoyOfCoding repository root. Build the grader first if its shaded
JAR is missing or stale:

```sh
./mvnw --batch-mode -pl grader -am package -DskipTests
```

Invoke the existing tool with all selected aggregate gradebook paths:

```sh
files=()
TARGET_TERM=winter2027
target_season=${TARGET_TERM%????}
target_year=${TARGET_TERM: -4}
case "$target_season" in
  summer) target_offset=0 ;;
  winter) target_offset=1 ;;
  *) echo "Invalid target term: $TARGET_TERM" >&2; exit 1 ;;
esac
target_key=$((target_year * 2 + target_offset))
for term_dir in "$GRADES_ROOT"/{winter,summer}20*; do
  [ -d "$term_dir" ] || continue
  term=$(basename "$term_dir")
  year=${term: -4}
  [ "$year" -ge 2022 ] || continue
  season=${term%????}
  case "$season" in
    summer) term_offset=0 ;;
    winter) term_offset=1 ;;
    *) continue ;;
  esac
  term_key=$((year * 2 + term_offset))
  [ "$term_key" -lt "$target_key" ] || continue
  [ -f "$term_dir/$term.xml" ] || continue
  files+=("$term_dir/$term.xml")
done
IFS=$'\n' files=($(printf '%s\n' "${files[@]}" | sort))

java -jar grader/target/grader-1.8.0-SNAPSHOT.jar \
  projectTimeEstimates \
  "${files[@]}" > /tmp/project-time-estimates.md
```

Use one specific grader JAR if multiple matching JARs exist; do not use a
sources, javadoc, or original JAR. Confirm that the selected files include
only one matching aggregate gradebook per term whose year is 2022 or later
before invoking Java.

The tool's output is the complete Markdown table. Its statistics use the
maximum submitted estimate per student/project and calculate count, average,
maximum, upper quartile, median, lower quartile, and minimum.

## Update the README

1. Review the generated table, especially its project columns. The current
   grader tool determines the column order and may include `AI Assisted` rather
   than an older `XML` column; do not silently relabel or merge columns.
2. Replace only the existing table under
   `### About how long do the projects take to complete?`; preserve the
   surrounding explanatory text and headings.
3. Validate that the README contains exactly one generated table, that its
   header and all seven statistic rows come directly from the tool output, and
   that no student names or grade data were copied into the README.
4. Run `git diff --check` and inspect the final diff. Do not commit unrelated
   changes.

Report the number of input XML files, the term range included, the generated
project columns, and the final table update. Never include student-identifying
data in the report.
