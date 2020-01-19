File buildLog = new File(basedir, "project/coverage/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedCoverageMessage = "Coverage checks have not been met"
if (!logText.contains(expectedCoverageMessage)) {
  throw new IllegalStateException("Expected to find: " + expectedCoverageMessage)
}

def unexpectedCoverageMessage = "classes missed count is"
if (logText.contains(unexpectedCoverageMessage)) {
  throw new IllegalStateException("Didn't find expected build output: " + unexpectedCoverageMessage)
}
