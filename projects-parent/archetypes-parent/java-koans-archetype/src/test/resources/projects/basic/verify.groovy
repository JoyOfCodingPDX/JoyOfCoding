package projects.basic

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport

def projectDir = new File(basedir, "project/basic")
File mvnwFile = new File(projectDir, "mvnw");
if (!mvnwFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwFile)
}

File mvnwCmdFile = new File(projectDir, "mvnw.cmd");
if (!mvnwCmdFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwCmdFile)
}

def walk = Files.walk(Paths.get(projectDir.toURI()))
List<Path> gradleFiles = walk.filter(new Predicate<Path>() {
  @Override
  boolean test(Path path) {
    return path.toString().contains("gradle");
  }
}).map(new Function<Path, String>() {
  @Override
  String apply(Path path) {
    path.toString();
  }
}).collect(Collectors.toList());


if (!gradleFiles.isEmpty()) {
  String s = "Found " + gradleFiles.size() + " gradle files: " +
    gradleFiles.stream().collect(Collectors.joining(", "));
  throw new IllegalStateException(s);
}