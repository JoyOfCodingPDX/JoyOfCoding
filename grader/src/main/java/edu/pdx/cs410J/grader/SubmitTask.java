package edu.pdx.cs410J.grader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.util.ArrayList;
import java.util.List;

/**
 * An Ant task for submitting projects.  This class follows the
 * JavaBean pattern that is required by Ant: public, zero-argument
 * constructor and setter methods for each of the task's attributes.
 *
 * <h2><a name="submit">Submit</a></h2>
 * <h3>Description</h3>
 *
 * <p>Submits project files to the Grader for grading.  A receipt
 * email is sent to the submitter.  Because this Ant task sends email,
 * the JavaMail libraries must be available to the task:</p>
 *
 * <pre>
 *     &lt;taskdef name=&quot;submit&quot; classname=&quot;edu.pdx.cs410J.grader.SubmitTask&quot;&gt;
 *       &lt;classpath&gt;
 *         &lt;pathelement location=&quot;${daves.jars.dir}/grader.jar&quot;/&gt;
 *         &lt;pathelement location=&quot;${daves.jars.dir}/mail.jar&quot;/&gt;
 *         &lt;pathelement location=&quot;${daves.jars.dir}/activation.jar&quot;/&gt;
 *       &lt;/classpath&gt;
 *    &lt;/taskdef&gt;
 * </pre>
 *
 * <h3>Parameters</h3>
 * 
 * <table border="1" cellpadding="2" cellspacing="0">
 *  <caption>Properties of this Ant Task</caption>
 *  <tr>
 *    <td valign="top"><b>Attribute</b></td>
 *    <td valign="top"><b>Description</b></td>
 *    <td align="center" valign="top"><b>Required</b></td>
 *  </tr>
 *  <tr>
 *    <td valign="top">project</td>
 *    <td valign="top">The name of the project to be submitted</td>
 *    <td align="center" valign="top">Yes</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">username</td>
 *    <td valign="top">The name of the submitter</td>
 *    <td align="center" valign="top">Yes</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">userid</td>
 *    <td valign="top">The login id of the submitter.  All submitted
 * files must reside in a directory with this name.</td>
 *    <td align="center" valign="top">Yes</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">email</td>
 *    <td valign="top">The email address of the submitter</td>
 *    <td align="center" valign="top">Yes</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">comment</td>
 *    <td valign="top">Information about the project that the
 * submitter wants the grader to know.  A longer comment can be
 * specified with a nexted &lt;comment&gt; element.</td>
 *    <td align="center" valign="top">No</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">server</td>
 *    <td valign="top">The name of the SMTP server used to send email</td>
 *    <td align="center" valign="top">No</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">debug</td>
 *    <td valign="top">Enabled verbose logging? ("true" or "false") </td>
 *    <td align="center" valign="top">No</td>
 *  </tr>
 *  <tr>
 *    <td valign="top">savezip</td>
 *    <td valign="top">Should the temporary zip file containing the
 * submitted files be saved? ("true" or "false")</td>
 *    <td align="center" valign="top">No</td>
 *  </tr>
 * <!--
 *  <tr>
 *    <td valign="top"></td>
 *    <td valign="top"></td>
 *    <td align="center" valign="top"></td>
 *  </tr>
 * -->
 * </table>
 *
 * <h2>Parameters specified as nested elements</h2>
 *
 * <h3>fileset (required)</h3>
 *
 * <p>An Ant <a
 * href="http://ant.apache.org/manual/CoreTypes/fileset.html">FileSet</a>
 * that specifies the files to be submitted.</p>
 *
 * <h3>comment</h3>
 *
 * <p>A longer comment that communicates some information from the
 * submitter to the grader.  Multiple nested &lt;comment&gt; elements
 * can be specified.  Each nested &lt;comment&gt; has a value
 * attribute that specifies the text of the comment:</p>
 *
 * <pre>
 * &lt;comment value=&quot;GUI file browser&quot;/&gt;
 * </pre>
 *
 * <h2>Examples</h2>
 *
 * <h3>Submit files for a project</h3>
 *
 * <pre>
 *     &lt;submit project=&quot;Project 4&quot; username=&quot;David Whitlock&quot;
 *             userid=&quot;whitlock&quot; email=&quot;whitlock@cs.pdx.edu&quot;&gt;
 *      &lt;comment value=&quot;Fixed command line parsing bug&quot;/&gt;
 *      &lt;fileset dir=&quot;${src.dir}&quot;&gt;
 *        &lt;include name=&quot;edu/pdx/cs410J/whitlock/*.java&quot;/&gt;
 *      &lt;/fileset&gt;
 *    &lt;/submit&gt;
 * </pre>
 *
 * @author David Whitlock
 * @since Spring 2006
 */
public class SubmitTask extends Task {

  /** The Submit object that does the actual work of submitting */
  private final Submit submit;

  /** The comments for this submission */
  private final List<Comment> comments = new ArrayList<Comment>();

  /** The files to be submitted */
  private final List<FileSet> filesets = new ArrayList<FileSet>();

  ///////////////////////  Constructors  ///////////////////////

  /**
   * Ant requires that all <code>Task</code>s have a public,
   * zero-argument constructor.  This constructor is invoked
   * reflectively when the ant build file is parsed.
   */
  public SubmitTask() {
    this.submit = new Submit();
  }

  /////////////////////  Instance Methods  ////////////////////

  /**
   * Sets the name of the SMTP server that is used to send emails
   */
  public void setServer(String serverName) {
    this.submit.setEmailServerHostName(serverName);
  }

  /**
   * Sets whether or not the progress of the submission should be
   * logged.
   */
  public void setDebug(boolean debug) {
    this.submit.setDebug(debug);
  }

  /**
   * Sets whether or not the zip file generated by the submission
   * should be saved.
   */
  public void setSavezip(boolean saveZip) {
    this.submit.setSaveZip(saveZip);
  }

  /**
   * Sets the comment for this submission
   */
  public void setComment(String value) {
    Comment comment = new Comment();
    comment.setValue(value);
    this.comments.add(comment);
  }

  /**
   * Sets the name of project being submitted
   */
  public void setProject(String projName) {
    this.submit.setProjectName(projName);
  }

  /**
   * Sets the name of the user who is submitting the project
   */
  public void setUserName(String userName) {
    this.submit.setUserName(userName);
  }
    
  /**
   * Sets the id of the user who is submitting the project
   */
  public void setUserid(String userId) {
    this.submit.setUserId(userId);
  }

  /**
   * Sets the email address of the user who is submitting the project
   */
  public void setEmail(String email) {
    this.submit.setUserEmail(email);
  }

  /**
   * Creates a new comment for this submission
   */
  public Comment createComment() {
    Comment comment = new Comment();
    this.comments.add(comment);
    return comment;
  }

  /**
   * Adds set of files to this submission
   */
  public void addFileSet(FileSet files) {
    this.filesets.add(files);
  }

  /**
   * Performs the submission using the configuration specified in this
   *  Ant task.
   */
  public void execute() throws BuildException {
    StringBuffer comment = new StringBuffer();
    for (Comment c : comments) {
      comment.append(c.getValue());
    }
    this.submit.setComment(comment.toString());

    for (FileSet fs : filesets) {
      DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
      String[] files = ds.getIncludedFiles();
      for (String file : files) {
        this.submit.addFile(file);
      }
    }

    try {
      this.submit.submit(false /* verify */);

    } catch (IllegalArgumentException ex) {
      throw new BuildException(ex.getMessage());

    } catch (Exception ex) {
      String s = "While submitting project";
      throw new BuildException(s, ex);
    }
  }

  //////////////////////  Inner Classes  ///////////////////

  /**
   * A little class that holds a comment.  This allows us to have
   * multiple, nested comments.
   */
  public static class Comment {
    /** The value of this comment */
    private String value;

    /**
     * Creates a new <code>Comment</code>
     */
    public Comment() {

    }

    /**
     * Returns the value of this comment
     */
    public String getValue() {
      return this.value;
    }

    /**
     * Sets the value of this comment
     */
    public void setValue(String value) {
      this.value = value;
    }

  }

}
