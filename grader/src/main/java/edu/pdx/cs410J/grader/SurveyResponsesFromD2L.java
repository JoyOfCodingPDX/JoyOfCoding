package edu.pdx.cs410J.grader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SurveyResponsesFromD2L {
  private LinkedHashMap<String, List<String>> questionsAndResponses = new LinkedHashMap<>();

  public Iterable<String> getResponsesTo(String question) {
    return questionsAndResponses.getOrDefault(question, Collections.emptyList());
  }

  public void noteQuestionAndResponse(String question, String response) {
    List<String> responses = this.questionsAndResponses.get(question);
    if (responses == null) {
      responses = new ArrayList<>();
      this.questionsAndResponses.put(question, responses);
    }

    responses.add(response);
  }

  public Iterable<String> getQuestions() {
    return this.questionsAndResponses.keySet();
  }
}
