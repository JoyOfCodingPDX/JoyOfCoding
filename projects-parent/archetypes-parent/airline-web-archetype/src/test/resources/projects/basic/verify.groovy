def projectDir = new File(basedir, "project/basic")

File targetDir = new File(projectDir, "target");

File jarFile = new File(targetDir, "airline.jar");
if (!jarFile.isFile()) {
  throw new FileNotFoundException("Couldn't find jar file: " + jarFile)
}