import java.util.jar.JarFile

File touchFile = new File( basedir, "target/touch.txt" );

assert touchFile.isFile()

File jarDir = new File( basedir, "target" )
File jarFile = jarDir.listFiles().find{it.isFile() && it=~/.jar$/ }
assert jarFile.exists()
assert jarFile.isFile()

def jarEntries = new JarFile(jarFile).entries()*.name
assert jarEntries.contains("edu/pdx/cs410J/it/TestClass.java")
assert jarEntries.contains("edu/pdx/cs410J/it/TestClass2.java")
assert jarEntries.contains("edu/pdx/cs410J/it/TestClass3.java")