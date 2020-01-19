File jarFile = new File(basedir, "project/basic/target/airline.jar");
if (!jarFile.isFile()) {
  throw new FileNotFoundException("Couldn't find jar file: " + jarFile)
}
