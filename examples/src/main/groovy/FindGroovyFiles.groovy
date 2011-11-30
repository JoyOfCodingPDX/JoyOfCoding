import java.io.FilenameFilter as FNF

static def findGroovyFiles(File root) {
  def groovyFiles = root.listFiles(
    { dir, name -> name.endsWith("groovy") } as FNF
  )
  for ( file in groovyFiles ) {
    println file
  }

  def subdirs = root.listFiles({ it.isDirectory() } as FileFilter)
  for ( subdir in subdirs ) {
    findGroovyFiles( subdir )
  }
}

static def main(args) {
  findGroovyFiles(new File(args[0]))
}