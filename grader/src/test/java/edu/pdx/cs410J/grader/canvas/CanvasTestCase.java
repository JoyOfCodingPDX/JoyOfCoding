package edu.pdx.cs410J.grader.canvas;

public abstract class CanvasTestCase {
  protected GradesFromCanvas.CanvasStudent createCanvasStudent(String... parameters) {
    String firstName = getParameter(parameters, 0, "First Name");
    String lastName = getParameter(parameters, 1, "Last Name");
    String loginId = getParameter(parameters, 2, "loginId");
    String canvasId = getParameter(parameters, 3, "canvasId");

    return GradesFromCanvas.newStudent()
      .setFirstName(firstName).setLastName(lastName)
      .setLoginId(loginId).setCanvasId(canvasId).create();
  }

  private String getParameter(String[] parameters, int index, String defaultValue) {
    if (index < parameters.length) {
      return parameters[index];

    } else {
      return defaultValue;
    }
  }
}
