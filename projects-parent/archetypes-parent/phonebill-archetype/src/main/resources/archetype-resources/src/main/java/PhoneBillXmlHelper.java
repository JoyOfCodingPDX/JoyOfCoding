#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs.joy.ProjectXmlHelper;

public class PhoneBillXmlHelper extends ProjectXmlHelper {

  /**
   * The Public ID for the Phone Bill DTD
   */
  protected static final String PUBLIC_ID =
    "-//Joy of Coding at PSU//DTD Phone Bill//EN";

  protected PhoneBillXmlHelper() {
    super(PUBLIC_ID, "phonebill.dtd");
  }
}
