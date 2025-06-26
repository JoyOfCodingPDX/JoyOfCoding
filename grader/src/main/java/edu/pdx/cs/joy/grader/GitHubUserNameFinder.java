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
    Matcher gitHubUrl = Pattern.compile("git@github.com:(.*)/.*\\.git").matcher(gitUrl);
    if (gitHubUrl.matches()) {
      this.gitHubUserName = gitHubUrl.group(1);

    } else {
      Matcher httpsUrl = Pattern.compile("https://github.com/(.*)/.*\\.git").matcher(gitUrl);
      if (httpsUrl.matches()) {
        this.gitHubUserName = httpsUrl.group(1);
      }
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
