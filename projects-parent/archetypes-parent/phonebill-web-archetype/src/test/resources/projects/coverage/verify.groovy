File buildLog = new File(basedir, "project/coverage/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

// The archetype provides complete code coverage!
def unexpectedCoverageMessage = "Coverage checks have not been met"
if (logText.contains(unexpectedCoverageMessage)) {
  throw new IllegalStateException("Unexpected build output: " + unexpectedCoverageMessage)
}