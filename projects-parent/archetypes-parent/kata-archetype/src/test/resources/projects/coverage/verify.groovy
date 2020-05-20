File buildLog = new File(basedir, "project/coverage/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedCoverage = "All coverage checks have been met."
if (!logText.contains(expectedCoverage)) {
  throw new IllegalStateException("Didn't find expected build output: " + expectedCoverage)
}
