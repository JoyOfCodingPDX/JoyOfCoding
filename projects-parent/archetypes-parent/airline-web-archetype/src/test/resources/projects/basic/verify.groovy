def projectDir = new File(basedir, "project/basic")
File mvnwFile = new File(projectDir, "mvnw");
if (!mvnwFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwFile)
}

File mvnwCmdFile = new File(projectDir, "mvnw.cmd");
if (!mvnwCmdFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwCmdFile)
}

File targetDir = new File(projectDir, "target");

File jarFile = new File(targetDir, "airline-client.jar");
if (!jarFile.isFile()) {
  throw new FileNotFoundException("Couldn't find jar file: " + jarFile)
}

String jarCommand = "java -jar ${jarFile}"
def execution = jarCommand.execute()
execution.waitFor()
String stderr = execution.err.text
if (!stderr.contains("Missing command line arguments")) {
  throw new IllegalStateException("Running jar returned \"" + stderr + "\"");
}