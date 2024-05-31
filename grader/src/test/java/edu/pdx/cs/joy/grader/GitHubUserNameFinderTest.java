package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GitHubUserNameFinderTest {

  @Test
  void findGitHubUserName() {
    GitHubUserNameFinder finder = new GitHubUserNameFinder();
    finder.startRemoteSection("origin");
    finder.property("url", "git@github.com:JoyOfCodingPDX/JoyOfCoding.git");

    assertThat(finder.getGitHubUserName(), equalTo("JoyOfCodingPDX"));
  }
}
