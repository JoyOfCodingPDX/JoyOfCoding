package edu.pdx.cs410J.grader;

import java.util.Optional;

public class GradesFromD2LImporter {
  public static void importGradesFromD2L(GradesFromD2L d2lGrades, GradeBook gradebook) {

    for (GradesFromD2L.D2LStudent d2LStudent : d2lGrades.getStudents()) {
      Student student = d2lGrades.findStudentInGradebookForD2LStudent(d2LStudent, gradebook);
      for (String quizName : d2LStudent.getQuizNames()) {
        Optional<Assignment> optional = d2lGrades.findAssignmentInGradebookForD2lQuiz(quizName, gradebook);
        Assignment quiz = optional.orElseThrow(() -> new IllegalStateException("No quiz name \"" + quizName + "\" in gradebook"));
        student.setGrade(quiz.getName(), new Grade(quiz, d2LStudent.getScore(quizName)));
      }
    }
  }
}
