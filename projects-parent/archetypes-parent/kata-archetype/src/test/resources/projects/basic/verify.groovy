def projectDir = new File(basedir, "project/basic")
File mvnwFile = new File(projectDir, "mvnw");
if (!mvnwFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwFile)
}

File mvnwCmdFile = new File(projectDir, "mvnw.cmd");
if (!mvnwCmdFile.isFile()) {
  throw new FileNotFoundException("Couldn't find mvn wrapper: " + mvnwCmdFile)
}

