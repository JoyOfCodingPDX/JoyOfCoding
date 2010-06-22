#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs399J.AbstractAppointmentBook;

/**
 * The main class for the CS399J appointment book Project
 */
public class Project1 {

  public static void main(String[] args) {
    AbstractAppointmentBook book;
    System.err.println("Missing command line arguments");
    for (String arg : args) {
      System.out.println(arg);
    }
    System.exit(1);
  }

}