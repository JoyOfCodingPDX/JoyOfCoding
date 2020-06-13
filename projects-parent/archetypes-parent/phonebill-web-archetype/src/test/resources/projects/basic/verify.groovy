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

File jarFile = new File(targetDir, "phonebill.jar");
if (!jarFile.isFile()) {
  throw new FileNotFoundException("Couldn't find jar file: " + jarFile)
}