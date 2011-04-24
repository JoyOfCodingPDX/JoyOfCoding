package edu.pdx.cs410J.grader;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

/**
 * This class contains a number of helper methods for testing projects
 * in CS410J.
 *
 * @author David Whitlock
 */
public class Tester {

  /**
   * The original <code>System.err</code>
   */
  PrintStream consoleErr = System.err;

  /**
   * The original <code>System.out</code>
   */
  PrintStream consoleOut = System.out;

  /**
   * The number of character columns in the log file.
   */
  private static final int WIDTH = 72;

  /**
   * Write output of tests and other stuff to here.
   */
  private PrintWriter log;

  /**
   * The student's id for whom we are loggin.
   */
  private String id;

  /**
   * Where to look for students' classes
   */
  private URL[] urls;

  /**
   * Are we finished?
   */
  private boolean done = false;

  private PrintStream ps;

  /**
   * Creates a new <code>Tester</code> that sends its output to a
   * given <code>PrintWriter</code>.  This is useful when you want to
   * dump to the console.  The "student's" id is
   * <code>"writer"</code>.
   */
  public Tester(PrintWriter pw) {
    this.id = "writer";
    this.log = pw;
  }
  

  /**
   * Creates a new <code>Tester</code> for a given student.  It will
   * create a log file in given directory.
   *
   * @param id
   *        The student's login id
   * @param logDir
   *        Directory into which the log file is placed.
   */
  public Tester(final String id, File logDir) {
    this.id = id;

    if (!logDir.isDirectory()) {
      throw new IllegalArgumentException(logDir + 
					 " is not a directory");
    }

    if (!logDir.exists()) {
      logDir.mkdirs();
    }

    String logFileName = id + ".log";
    File logFile = new File(logDir, logFileName);
    

    // Create a PrintStream around the log file.  Set System.out and
    // System.err to refer to this guy.
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(logFile);

    } catch (IOException ex) {
      throw new IllegalArgumentException("Could not open " +
					 logFileName);
    }

    /**
     * Inner class used to prevent file stream from being closed until
     * I say so.  Other code may attempt to close System.out which
     * result in this file stream being closed. 
     */
    class CheckedPrintStream extends PrintStream {
      public CheckedPrintStream(OutputStream stream) {
	super(stream);
      }

      public void close() {
	if(Tester.this.done) {
//  	  Tester.this.consoleOut.println("Tester " + id + 
//  					 " is closing stream");
	  super.close();
	}
	
//  	Tester.this.consoleOut.println("Tester " + id + 
//  				       " is not done yet");
      }
    }


//      this.ps = System.out;
    this.ps = new CheckedPrintStream(fos);
    System.setOut(ps);
    System.setErr(ps);

    // Create a PrintWriter around the PrintStream
    this.log = new PrintWriter(ps, true);
  }

  /**
   * Parses a file path and creates an array of <code>URL</code>s
   * representing each file on the path.
   */
  @SuppressWarnings("deprecation")
public static URL[] parseURLPath(String path) {
    if (path == null) {
      return new URL[0];
    }

    StringTokenizer st = new StringTokenizer(path,
					     File.pathSeparator);
    URL[] urls = new URL[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      File file = new File(st.nextToken());
      try {
	urls[i] = file.toURL();

      } catch (MalformedURLException ex) {
	System.err.println("** Malformatted URL: " + file);
      }
    }
    
    return urls;
  }

  /**
   * Sets the URLs on which to search for students' classes
   */
  public void setURLs(URL[] urls) {
    this.urls = urls;
  }

  /**
   * Executes a static method of a given name in a given class with a
   * given set of parameters.
   */
  public Object executeStatic(String className, String methodName, 
			    Class[] paramTypes, Object[] params) {

    // By default, print a banner
    return executeStatic(className, methodName, paramTypes, params, true);
  }


  /**
   * Executes a static method of a given name in a given class with a
   * given set of parameters.
   *
   * @param printBanner
   *        Do we print a banner message?
   */
  public Object executeStatic(String className, String methodName, 
			      Class[] paramTypes, Object[] params,
			      boolean printBanner) {

    ClassLoader loader = new URLClassLoader(this.urls);

    // First load the class and the desired method
    Class c = null;
    try {
      c = Class.forName(className, true, loader);

    } catch (ClassNotFoundException ex) {
      System.err.println("** Could not load " + className);
      ex.printStackTrace(System.err);
      return null;
    }

    Method m = null;
    try {
      m = c.getMethod(methodName, paramTypes);

    } catch (NoSuchMethodException ex) {
      System.err.println("** Could not find method " + methodName + 
			 " in " + className);
      ex.printStackTrace(System.err);
      return null;
    }

    if (printBanner) {
      // Print out some information
      this.printBanner("Executing " + className + "." + methodName +
		       "()", '-');
    }

    // Run the method
    Object result = null;
    try {
      try {
        result = m.invoke(null, params);

      } catch (InvocationTargetException ex) {
        throw ex.getTargetException();
      }

    } catch (TesterExitException ex) {
      // That's okay, we're just exiting from one invocation of the
      // main method

    } catch (IllegalAccessException ex) {
      System.err.println("** IllegalAccessException while invoking " +
                         className + "." + methodName + "()");
      ex.printStackTrace(System.err);
      result = null;

    } catch (Exception ex) {
      // Log everything else
      System.err.println("** Exception while invoking " +
                         className + "." + methodName + "()");
      ex.printStackTrace(System.err);
      result = null;

    } catch (Throwable ex) {
      // EEEP!!!
      System.err.println("*** SEVERE ERROR!!!");
      ex.printStackTrace(System.err);
      System.exit(1);
    }

    System.out.flush();
    System.err.flush();

    this.log.println("");
    this.log.flush();
    return result;
  }

  /**
   * Executes the <code>main</code> method of the class with the given
   * name in its own thread.
   *
   * @return The <code>Thread</code> in which the <code>main</code>
   *         method is running.
   */
  public Thread executeMainInThread(final String className, 
				  final String[] args) {

    this.printlnCentered("Executing " + className);

    Runnable r = new Runnable() {
	public void run() {
	  try {
	    Tester.this.executeMain(className, args);

	  } catch (Throwable t) {
	    return;
	  }
	}
      };
    
    Thread thread = new Thread(r);
    thread.start();

    return thread;
  }

  /**
   * Waits for a given number of seconds.  Returns <code>false</code>
   * if the thread was interrupted.
   */
  public boolean wait(int seconds) {
    try {
      Thread.sleep(1000 * seconds);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Executes the <code>main</code> method of the class with the given
   * name with the given arguments.
   */
  public void executeMain(String className, String[] args) {
//      this.consoleOut.println("out: " + System.out);
//      this.consoleOut.println("err: " + System.err);

//      System.setOut(this.ps);
//      System.setErr(this.ps);

    // Create a new ClassLoader to load the student's classes.  This
    // way the class is initialized each time this method is invoked
    // and we don't need to worry about the old values of static
    // fields.
    ClassLoader loader = new URLClassLoader(urls);
//      ClassLoader loader = sysLoader;

    // First load the class and get its main method
    Class c = null;
    try {
      c = Class.forName(className, true, loader);

    } catch (ClassNotFoundException ex) {
      System.err.println("** Could not load " + className);
      ex.printStackTrace(System.err);
      return;
    }

    Method main = null;
    try {
      Class stringArray = args.getClass();
      Class[] paramTypes = {stringArray};

      main = c.getMethod("main", paramTypes);

    } catch (NoSuchMethodException ex) {
      System.err.println("** Could not find main method in " +
			 className);
      ex.printStackTrace(System.err);
      return;
    }

    // Process the arguments to replace any macros
    args = expandMacros(args);

    // Print out args
    StringBuffer sb = new StringBuffer();
    for (int j = 0; j < args.length; j++) {
      boolean needQuotes = false;

      if (args[j].indexOf(" ") != -1) {
        needQuotes = true;
      }

      if (needQuotes) {
        sb.append("\"");
      }

      sb.append(args[j]);

      if (needQuotes) {
        sb.append("\"");
      }

      sb.append(" ");
    }
 
   this.log.println("Command line: " + sb.toString().trim());
    this.log.println("");
    this.log.println("Program output:");

    // Invoke the main method with the given arguments.
    try {
      Object[] actuals = {args};
      
      try {
        main.invoke(null, actuals);
    
      } catch (InvocationTargetException ex) {
        throw ex.getTargetException();
      }

      System.exit(42);

    } catch (TesterExitException ex) {
	// That's okay...
//         this.consoleOut.println(thr);
      
    } catch (IllegalAccessException ex) {
      System.err.println("** IllegalAccessException while invoking " +
			 "main");
      ex.printStackTrace(System.err);
      return;

    } catch (Exception ex) {
      System.err.println("** Exception while invoking main");
      ex.printStackTrace(System.err);
      return;

    } catch (Throwable ex) {
      // EEEP!!!
      System.err.println("*** SEVERE ERROR!!!");
      ex.printStackTrace(System.err);
      System.exit(1);
    }
//        this.consoleOut.println("Finally: " + args);
//        System.out.println("Finally: " + args);
    System.out.flush();
    System.err.flush();
    this.log.println("");
  }

  /**
   * Go through an array of <code>String</code>s and expand macros
   * such as "{STUDENT}".
   *
   * @return A clone of the <code>args</code> array with macros
   *         expanded
   */
  String[] expandMacros(String[] args) {
    Map<String, String> map = new HashMap<String, String>();
    map.put("{STUDENT}", this.id);

    args = (String[]) args.clone();

    for (int i = 0; i < args.length; i++) {
      Iterator keys = map.keySet().iterator();
      while (keys.hasNext()) {
	String key = (String) keys.next();
	String value = (String) map.get(key);
	int index = args[i].indexOf(key);
	for(int j = 0; j < 20 && index != -1; j++) {
	  // Replace key with value
	  StringBuffer sb = new StringBuffer(args[i]);
	  sb.replace(index, index + key.length(), value);
	  args[i] = sb.toString();

	  index = args[i].indexOf(key);
	}
      }
    }

    return args;
  }

  /**
   * Prints a left justified line of text to the log file.
   */
  public void println(Object o) {
    this.log.println(o);
  }

  /**
   * Prints a left justified line of text to the log file.
   */
  public void println(String text) {
    this.log.println(text);
  }

  /**
   * Prints a line of text to the log file and centers it.
   */
  public void printlnCentered(String text) {
    printBanner(text, ' ');
  }

  /**
   * Prints a line of text to the log file, centers it, around
   * surrounds it with a given character.
   */
  public void printBanner(String text, char c) {
    int length = text.length();

    // Don't bother centering if the text is wider than the log.
    if (length < WIDTH) {
      int indent = (WIDTH - length) / 2;

      for (int i = 0; i < indent - 1; i++) {
	this.log.print(c);
      }
      this.log.print(' ');
      this.log.flush();
    }

    this.log.print(text);

    if (length < WIDTH) {
      int indent = (WIDTH - length) / 2;

      this.log.print(' ');
      for (int i = 0; i < indent - 1; i++) {
	this.log.print(c);
      }
      this.log.flush();
    }

    this.log.println("");
  }

  /**
   * Copies one file to another.  If the destination file exists, it
   * is overridden.
   */
  public void cp(File srcFile, File destFile) {
    // Copy using streams in case we ever need to copy binary files.
    FileInputStream src = null;
    try {
      src = new FileInputStream(srcFile);

    } catch (FileNotFoundException ex) {
      String s = "Could not find file " + srcFile;
      throw new IllegalArgumentException(s);
    }

    try {
      FileOutputStream dest = new FileOutputStream(destFile);
      
      byte[] buffer = new byte[1024];
      int count = src.read(buffer);
      while (count != -1) {
	dest.write(buffer, 0, count);
	count = src.read(buffer);
      }
      src.close();
      dest.close();

    } catch (IOException ex) {
      String s = "IOException " + ex;
      throw new IllegalArgumentException(s);
    }
  }

  /**
   * "Cats" a file to the log.  That is, copies its contents to the
   * log.
   *
   * @param file
   *        <code>File</code> to be catted
   */
  public void cat(File file) {
    printlnCentered("File: " + file.getName());
    this.log.println("");

    if (!file.exists()) {
      printlnCentered("Does not exist!");
      return;
    }

    try {
      FileReader fr = new FileReader(file);
      BufferedReader br = new BufferedReader(fr);

      while (br.ready()) {
	String line = br.readLine();
	this.log.println(line);
      }

    } catch (FileNotFoundException ex) {
      String s = "Could not find file: " + file;
      throw new IllegalArgumentException(s);

    } catch (IOException ex) {
      throw new IllegalArgumentException(ex.toString());
    }

    this.log.println("\n");
  }

  /**
   * Signifies that this <code>Tester</code> is done
   */
  void done() {
    this.done = true;
  }

  /**
   * Make sure we're done when this <code>Tester</code> gets garbage
   * collected.  This way, the stream will always get closed.
   */
  public void finalize() {
    this.done = true;
  }

  /**
   * Load a class from a given directory
   */
  public static void main(String[] args) {
    String url = args[0];
    String className = args[1];

    // Install a TesterSecurityManager that will allow us to run a
    // main method multiple times without exiting.
    TesterSecurityManager tsm = new TesterSecurityManager();
    System.setSecurityManager(tsm);

    Tester tester = new Tester(new PrintWriter(System.out, true));
    tester.setURLs(Tester.parseURLPath(url));

    String[] classArgs = { "Hello", "World" };
    tester.executeMain(className, classArgs);    
  }

  /**
   * Old Test Program.
   */
  public static void main0(String[] args) {
    // Make a new Tester
    String userDir = System.getProperty("user.dir");
    File cwd = new File(userDir);
    Tester tester = new Tester("test", cwd);

    System.out.println("Does System.out work?");
    System.err.println("Does System.err work?");

    tester.printBanner("Catting a file!", '*');

    // Cat the file given in args[0]
    if (args.length > 0) {
      File file = new File(args[0]);
      tester.cat(file);
    }
  }
}
