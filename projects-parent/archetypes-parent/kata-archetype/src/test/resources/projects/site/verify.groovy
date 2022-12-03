def projectDir = new File(basedir, "project/site")
def indexDotHtml = new File(projectDir, "target/site/index.html")
if (!indexDotHtml.exists()) {
  throw new FileNotFoundException("Couldn't find index.html " + indexDotHtml)
}

String html = indexDotHtml.text

def expectedText = "Distribution Management"
if (!html.contains(expectedText)) {
  throw new IllegalStateException("Didn't fine expected text: " + expectedText)
}

expectedText = "apache-maven-fluido"
if (!html.contains(expectedText)) {
  throw new IllegalStateException("Didn't fine expected text: " + expectedText)
}