package edu.pdx.cs410J.airline;

import edu.pdx.cs410J.ProjectXmlHelper;

/**
 * This class provides easy access to the SYSTEM and PUBLIC ids for the
 * Airline XML DTD and implements some convenient error handling methods.
 */
public class AirlineXmlHelper extends ProjectXmlHelper {

  /** The System ID for the Family Tree DTD */
  protected static final String SYSTEM_ID =
    "http://www.cs.pdx.edu/~whitlock/dtds/airline.dtd";

  /** The Public ID for the Family Tree DTD */
  protected static final String PUBLIC_ID =
    "-//Portland State University//DTD CS410J Airline//EN";


  public AirlineXmlHelper() {
    super(PUBLIC_ID, SYSTEM_ID, "airline.dtd");
  }
}
