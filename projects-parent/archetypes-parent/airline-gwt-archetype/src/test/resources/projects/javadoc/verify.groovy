File buildLog = new File(basedir, "project/javadoc/build.log");
if (!buildLog.isFile()) {
  throw new FileNotFoundException("Couldn't find build log: " + buildLog)
}

String logText = buildLog.text

def expectedJavaDoc = "A basic GWT class that makes sure that we can send an airline back"
if (!logText.contains(expectedJavaDoc)) {
  throw new IllegalStateException("Didn't find expected JavaDoc: " + expectedJavaDoc)
}
