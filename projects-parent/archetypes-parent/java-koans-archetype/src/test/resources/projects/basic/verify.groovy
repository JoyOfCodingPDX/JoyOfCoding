package projects.basic

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

def projectDir = new File(basedir, "project/basic")
File mvnwFile = new File(projectDir, "mvnw");
if (!mvnwFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwFile)
}

File mvnwCmdFile = new File(projectDir, "mvnw.cmd");
if (!mvnwCmdFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwCmdFile)
}

validateNoFilesInDirectoryWithNameContaining(projectDir, "gradle")
validateNoFilesInDirectoryWithNameContaining(projectDir, "README.md")

private static void validateNoFilesInDirectoryWithNameContaining(File projectDir, String query) {
  def walk = Files.walk(Paths.get(projectDir.toURI()))
  List<Path> gradleFiles = walk.filter(new Predicate<Path>() {
    @Override
    boolean test(Path path) {
      return path.toString().contains(query);
    }
  }).map(new Function<Path, String>() {
    @Override
    String apply(Path path) {
      path.toString();
    }
  }).collect(Collectors.toList());


  if (!gradleFiles.isEmpty()) {
    String s = "Found " + gradleFiles.size() + " " + query + " files: " +
            gradleFiles.stream().collect(Collectors.joining(", "));
    throw new IllegalStateException(s);
  }
}

