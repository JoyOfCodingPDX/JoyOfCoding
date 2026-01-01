def projectDir = new File(basedir, "project/basic")
File mvnwFile = new File(projectDir, "mvnw");
if (!mvnwFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwFile)
}

File mvnwCmdFile = new File(projectDir, "mvnw.cmd");
if (!mvnwCmdFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwCmdFile)
}

String jarCommand = "java -jar ${projectDir}/target/basic-0.1-SNAPSHOT.jar"
def execution = jarCommand.execute()
execution.waitFor()
String stderr = execution.err.text
if (!stderr.contains("Missing appointment book information")) {
  throw new IllegalStateException("Running jar returned \"" + stderr + "\"");
}