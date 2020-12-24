File buildLog = new File(basedir, "project/coverage/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedCoverage = "Rule violated for bundle coverage: instructions covered ratio"
if (!logText.contains(expectedCoverage)) {
  throw new IllegalStateException("Didn't find expected build output: " + expectedCoverage)
}
