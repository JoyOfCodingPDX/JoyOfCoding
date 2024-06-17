package edu.pdx.cs.joy.grader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHubUserNameFinder implements GitConfigParser.Callback {
  private String gitHubUserName;
  private boolean isInOrigin;

  @Override
  public void startCoreSection() {

  }

  @Override
  public void property(String name, String value) {
    if (name.equals("url") && this.isInOrigin) {
      extractGitHubUserNameFrom(value);
    }
  }

  private void extractGitHubUserNameFrom(String gitUrl) {
    Pattern pattern = Pattern.compile("git@github.com:(.*)/.*\\.git");
    Matcher matcher = pattern.matcher(gitUrl);
    if (matcher.matches()) {
      this.gitHubUserName = matcher.group(1);
    }
  }

  @Override
  public void endSection(String name) {
    this.isInOrigin = false;
  }

  @Override
  public void startRemoteSection(String name) {
    if (name.equals("origin")) {
      this.isInOrigin = true;
    }
  }

  public String getGitHubUserName() {
    return gitHubUserName;
  }
}
