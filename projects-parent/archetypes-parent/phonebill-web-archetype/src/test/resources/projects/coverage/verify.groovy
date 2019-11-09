File buildLog = new File(basedir, "project/coverage/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedJavaDoc = "Coverage checks have not been met"
if (!logText.contains(expectedJavaDoc)) {
  throw new IllegalStateException("Didn't find expected build output: " + expectedJavaDoc)
}
