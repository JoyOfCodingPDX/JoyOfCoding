File buildLog = new File(basedir, "project/basic/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedOutput = "The main class for the CS410J airline Project"
if (!logText.contains(expectedOutput)) {
  throw new IllegalStateException("Didn't find expected output: " + expectedOutput)
}
