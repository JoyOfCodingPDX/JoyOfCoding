package edu.pdx.cs.joy.grader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GitConfigParserTest {

  @Test
  void canParseCoreSectionOfGitConfig() throws IOException {
    String config =
      "[core]\n" +
      "        repositoryformatversion = 0\n" +
      "        filemode = true\n";
    GitConfigParser parser = new GitConfigParser(new StringReader(config));
    GitConfigParser.Callback callback = mock(GitConfigParser.Callback.class);
    parser.parse(callback);

    verify(callback).startCoreSection();
    verify(callback).property("repositoryformatversion", "0");
    verify(callback).property("filemode", "true");
    verify(callback).endSection("core");
  }

  @Test
  void canParseRemoteOriginSectionOfGitConfig() throws IOException {
    String config =
      "[remote \"origin\"]\n" +
        "        url = git@github.com:JoyOfCodingPDX/JoyOfCoding.git\n" +
        "        fetch = +refs/heads/*:refs/remotes/origin/*";
    GitConfigParser parser = new GitConfigParser(new StringReader(config));
    GitConfigParser.Callback callback = mock(GitConfigParser.Callback.class);
    parser.parse(callback);

    verify(callback).startRemoteSection("origin");
    verify(callback).property("url", "git@github.com:JoyOfCodingPDX/JoyOfCoding.git");
    verify(callback).endSection("origin");
  }

  @Test
  void canParseRealGitConfig() throws IOException {
    InputStream resource = getClass().getResourceAsStream("gitConfig.txt");

    GitConfigParser parser = new GitConfigParser(new InputStreamReader(resource));
    GitConfigParser.Callback callback = mock(GitConfigParser.Callback.class);
    parser.parse(callback);

    verify(callback).startCoreSection();
    verify(callback).property("repositoryformatversion", "0");
    verify(callback).property("filemode", "true");
    verify(callback).endSection("core");

    verify(callback).startRemoteSection("origin");
    verify(callback).property("url", "git@github.com:JoyOfCodingPDX/JoyOfCoding.git");
    verify(callback).endSection("origin");

  }
}
