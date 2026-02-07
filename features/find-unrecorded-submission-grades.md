## Feature Description

This feature provides a way to identify submission grades that have not been recorded in the system. It helps
instructors ensure that all student submissions are accounted for and graded appropriately.

This is a new feature of the FindUngradedSubmissions tool, which scans through student submissions and compares them
against recorded grades to identify any discrepancies.

This will feature will require a new command line parameter to the main() method of FindUngradedSubmissions. The first
parameter will now be the name of a gradebook XML file that contains the recorded grades. Subsequent parameters will
continue to be the names of one or more submission files/directories to scan for untested, ungraded, and now unrecorded
grades.

Here's the new command line usage:

```
$ java -jar target/grader-1.5.2-SNAPSHOT.jar findUngradedSubmissions
Usage: java FindUngradedSubmissions -includeReason gradeBookXmlFile submissionZipOrDirectory+
```

The FindUngradedSubmissions class will now output the names of the .out files whose grades do not match what has been
recorded in the student's gradebook XML file.

```
0 submissions need to be tested: 
0 submissions need to be graded:
3 submissions have unrecorded grades:
  ./fred.out
  ./jane.out
  ./alex.out
```

## Implementation Ideas

The `SubmissionAnalysis` record will need a new gradeNeedsToBeRecorded() property.

The gradebook XML file should be parsed into a GradeBook object. Information about student grades can be obtained from
the GradeBook.

The name of the testOutput file begins with the student's login ID, which can be used to look up the recorded grade in
the GradeBook XML file. For instance, the student id of `fred` corresponds to the `fred.out` test output file.

In order to determine if a submission's grade is unrecorded, we'll need to know which project assigment is associated
with the testOutput (`.out`) file. The project can be determined from a line that looks like this in the `.out` file:

```
              The Joy of Coding Project 1: edu.pdx.cs410J.studentId.Project1
```

In this example, the project assignment is "Project1". This is the name of the assignment in the GradeBook XML file.

The grade should be considered unrecorded if the recorded grade is missing or different from the grade determined in the
GradeBook XML file.

## Test Cases

If a submission hasn't been graded yet, it should not be considered unrecorded.

If the submission has been graded (that is, a grade appears in the testOutput `.out` file) and there is no grade for the
assignment in the GradeBook, the submission should be considered unrecorded.

If the submission has been graded and the grade in the GradeBook is different from the grade in the testOutput `.out`,
the submission should be considered unrecorded.

If the submission has been graded and the grade in the GradeBook matches the grade in the testOutput `.out`, the
submission
should not be considered unrecorded.

There should be an end-to-end integration test for that includes parsing a gradebook XML file and multiple submission
files (.out) that verify that some .out files have grades that need to be recorded and other .out files don't. This
test should be implemented in a new integration test class called `FindUnrecordedSubmissionGradesIT` in the src/it/java
directory. It should use the invokeMain() from InvokeMainTestCase to run the FindUngradedSubmissions main() method with
the appropriate command line arguments and validate that the expected output is written to standard output. The files
that the test uses should be placed in the src/it/resources directory. Or they could be generated programmatically by
the test and placed in a temporary directory injected with JUnit 5's @TempDir annotation.

## Implementation Steps

### 1. Update the `SubmissionAnalysis` record

Add a new boolean property `gradeNeedsToBeRecorded` to the `SubmissionAnalysis` record (around line 196).

**Changes:**

- Add `boolean gradeNeedsToBeRecorded` as the fifth parameter to the record
- Update all places that construct `SubmissionAnalysis` objects to pass `false` for this parameter initially

### 2. Create a new interface `GradeBookProvider`

Add a new interface in the `FindUngradedSubmissions` class to provide access to the GradeBook.

**Interface signature:**

```java
interface GradeBookProvider {
  Optional<GradeBook> getGradeBook();
}
```

This allows for testability by mocking the gradebook access in tests.

### 3. Add GradeBookProvider to the constructor

Update the `FindUngradedSubmissions` constructor to accept an optional `GradeBookProvider` parameter.

**Changes:**

- Add `GradeBookProvider` field to the class (around line 24-27)
- Update the `@VisibleForTesting` constructor (around line 30) to accept a `GradeBookProvider` parameter
- Update the default constructor (around line 36) to pass `null` for the gradebook provider

### 4. Extend `TestOutputDetails` record

Add fields to capture the project assignment name and the grade from the test output file.

**Changes:**

- Add `String projectName` field to the `TestOutputDetails` record (around line 193)
- Add `Double grade` field to the `TestOutputDetails` record (around line 193)
- Update all places that construct `TestOutputDetails` objects

### 5. Update `TestOutputDetailsProviderFromTestOutputFile`

Modify the `TestOutputDetailsProviderFromTestOutputFile` class (around line 233) to extract:

- The project name from lines matching the pattern: `The Joy of Coding Project \\d+: edu.pdx.cs.\\w+.\\w+.(\\w+)`
- The grade value (already has `parseGrade` method at line 271)

**Changes to `TestOutputDetailsCreator` inner class:**

- Add `String projectName` field
- Add `Double grade` field
- In the `accept(String line)` method, add logic to:
    - Extract project name using a regex pattern
    - Capture the grade value (already extracts it, just need to store it)
- Update `createTestOutputDetails()` to include the new fields

### 6. Update the `analyzeSubmission` method

Modify the `analyzeSubmission` method (around line 38) to determine if a grade needs to be recorded.

**New logic after determining `needsToBeGraded`:**

```java
boolean gradeNeedsToBeRecorded = false;
if(!needsToBeGraded &&gradeBookProvider !=null){
gradeNeedsToBeRecorded =

checkIfGradeNeedsRecording(submission, testOutput);
}
```

### 7. Create a new method `checkIfGradeNeedsRecording`

Add a new private method to check if a grade needs to be recorded.

**Method signature:**

```java
private boolean checkIfGradeNeedsRecording(SubmissionDetails submission, TestOutputDetails testOutput)
```

**Logic:**

- Return false if testOutput doesn't have a project name or grade
- Get the GradeBook from the provider (return false if not available)
- Get the student from the gradebook using `submission.studentId()`
- If student not found, return true (unrecorded)
- Get the grade for the assignment using `student.getGrade(testOutput.projectName())`
- If grade is null, return true (unrecorded)
- If grade.getScore() != testOutput.grade(), return true (unrecorded)
- Otherwise return false

### 8. Update the `main` method

Modify the `main` method to:

- Parse the new command-line parameter for the gradebook XML file
- Create a `GradeBookProvider` implementation that loads the gradebook
- Pass the provider to the `FindUngradedSubmissions` constructor

**Changes:**

- Update usage message (around line 89) to show the new syntax
- Parse the first non-option argument as the gradebook XML file path
- Remaining arguments are submission files/directories
- Create a `GradeBookProviderFromXmlFile` implementation
- Track `gradeNeedsToBeRecorded` submissions in the analysis loop (around line 111)
- Add a third call to `printOutAnalyses` for unrecorded grades

### 9. Create `GradeBookProviderFromXmlFile` implementation

Add a new static inner class that implements `GradeBookProvider`.

**Implementation:**

```java
private static class GradeBookProviderFromXmlFile implements GradeBookProvider {
  private final String xmlFilePath;
  private GradeBook gradeBook;

  GradeBookProviderFromXmlFile(String xmlFilePath) {
    this.xmlFilePath = xmlFilePath;
  }

  @Override
  public Optional<GradeBook> getGradeBook() {
    if (gradeBook == null && xmlFilePath != null) {
      try {
        XmlGradeBookParser parser = new XmlGradeBookParser(xmlFilePath);
        gradeBook = parser.parse();
      } catch (Exception e) {
        System.err.println("Error loading gradebook: " + e.getMessage());
        return Optional.empty();
      }
    }
    return Optional.ofNullable(gradeBook);
  }
}
```

### 10. Update test file imports and add new tests

In `FindUngradedSubmissionsTest.java`, add test cases for the new functionality:

**Test cases to add:**

- `submissionWithUngradedTestOutputDoesNotNeedToBeRecorded()` - verify that if submission isn't graded, it's not
  considered unrecorded
- `submissionWithGradeNotInGradeBookNeedsToBeRecorded()` - verify missing grade in gradebook is detected
- `submissionWithDifferentGradeInGradeBookNeedsToBeRecorded()` - verify mismatched grades are detected
- `submissionWithMatchingGradeInGradeBookDoesNotNeedToBeRecorded()` - verify matching grades are not flagged
- `parseProjectNameFromTestOutputLine()` - verify project name extraction works
- `testOutputDetailsIncludesProjectNameAndGrade()` - verify TestOutputDetails captures all needed info

### 11. Integration Testing

Create or update integration tests to verify:

- Command-line parsing with the new gradebook parameter
- End-to-end flow with actual XML files
- Output formatting shows the three categories correctly

### Summary of Key Classes to Modify

1. `FindUngradedSubmissions.java` - main implementation
2. `FindUngradedSubmissionsTest.java` - unit tests
3. New imports needed:
    - `edu.pdx.cs.joy.grader.gradebook.GradeBook`
    - `edu.pdx.cs.joy.grader.gradebook.Grade`
    - `edu.pdx.cs.joy.grader.gradebook.Student`
    - `edu.pdx.cs.joy.grader.gradebook.XmlGradeBookParser`
    - `java.util.Optional`
