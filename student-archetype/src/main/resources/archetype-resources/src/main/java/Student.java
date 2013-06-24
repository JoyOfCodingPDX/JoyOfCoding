#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.lang.Human;

import java.util.ArrayList;
                                                                                    
/**                                                                                 
 * This class is represents a <code>Student</code>.                                 
 */                                                                                 
public class Student extends Human {                                                
                                                                                    
  /**                                                                               
   * Creates a new <code>Student</code>                                             
   *                                                                                
   * @param name                                                                    
   *        The ${artifactId}'s name                                                      
   * @param classes                                                                 
   *        The names of the classes the ${artifactId} is taking.  A ${artifactId}              
   *        may take zero or more classes.                                          
   * @param gpa                                                                     
   *        The ${artifactId}'s grade point average                                       
   * @param gender                                                                  
   *        The ${artifactId}'s gender ("male" or "female", case insensitive)             
   */                                                                               
  public Student(String name, ArrayList classes, double gpa, String gender) {

  }

  /**                                                                               
   * All ${artifactId}s say "This class is too much work"                                 
   */
  @Override
  public String says() {                                                            

  }
                                                                                    
  /**                                                                               
   * Returns a <code>String</code> that describes this                              
   * <code>Student</code>.                                                          
   */                                                                               
  public String toString() {

  }

  /**
   * Main program that parses the command line, creates a
   * <code>Student</code>, and prints a description of the ${artifactId} to
   * standard out by invoking its <code>toString</code> method.
   */
  public static void main(String[] args) {

  }
}