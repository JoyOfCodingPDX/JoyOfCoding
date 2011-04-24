package edu.pdx.cs410J;

import java.util.*;

/**
 * This class is used to get the name of airport from its three-letter
 * code.
 */
public class AirportNames {

  /** The singleton instance of AirportNames */
  private static AirportNames airportNames = null;

  ////////////////////////  Instance Fields  /////////////////////////

  /** Maps three-letter code to airport name */
  private final Map<String, String> names;

  /////////////////////////  Constructors  //////////////////////////

  /**
   * Creates a new <code>AirportNames</code> and fills in all of the
   * names.
   */
  private AirportNames() {
    Map<String, String> names = new java.util.TreeMap<String, String>();

    names.put("ABE", "Allentown, PA");
    names.put("ABQ", "Albuquerque, NM");
    names.put("ABI", "Abilene, TX");
    names.put("ACT", "Waco, TX");
    names.put("ALB", "Albany, NY");
    names.put("AMA", "Amarillo, TX");
    names.put("ANC", "Anchorage, AK");
    names.put("ATL", "Atlanta, GA");
    names.put("ATW", "Appleton, WI");
    names.put("AUS", "Austin, TX");
    names.put("AVP", "Wilkes Barre, PA");
    names.put("AZO", "Kalamazoo, MI");
    names.put("BDL", "Hartford, CT");
    names.put("BFL", "Bakersfield, CA");
    names.put("BGR", "Bangor, ME");
    names.put("BHM", "Birmingham, AL");
    names.put("BIL", "Billings, MT");
    names.put("BMI", "Bloomington, IL");
    names.put("BNA", "Nashville, TN");
    names.put("BOI", "Boise, ID");
    names.put("BOS", "Boston, MA");
    names.put("BPT", "Beaumont/Port Arthur, TX");
    names.put("BTR", "Batton Rouge, LA");
    names.put("BTV", "Burlington, VT");
    names.put("BUF", "Buffalo, NY");
    names.put("BUR", "Burbank, CA");
    names.put("BWI", "Baltimore, MD");
    names.put("CAE", "Columbia, SC");
    names.put("CAK", "Akron/Canton, OH");
    names.put("CHA", "Chattanooga, TN");
    names.put("CHS", "Charleston, SC");
    names.put("CID", "Cedar rapids, IA");
    names.put("CLE", "Cleveland, OH");
    names.put("CLT", "Charlotte, NC");
    names.put("CMH", "Columbus, OH");
    names.put("CMI", "Champaign/Urbana, IL");
    names.put("COS", "Colorado Springs, CO");
    names.put("CRP", "Corpus Christi, TX");
    names.put("CVG", "Cincinnati, OH");
    names.put("CWA", "Wausau/Stevens Point, WI");
    names.put("DAY", "Dayton, OH");
    names.put("DBQ", "Dubuque, IA");
    names.put("DCA", "Washington DC (National)");
    names.put("DEN", "Denver, CO");
    names.put("DET", "Detroit, MI (City)");
    names.put("DFW", "Dallas/Ft. Worth, TX");
    names.put("DRO", "Durango, CO");
    names.put("DSM", "Des Moines, IA");
    names.put("DTW", "Detroit, MI (Metro)");
    names.put("DUT", "Dutch Harbor, AK");
    names.put("EGE", "Vail, CO");
    names.put("ELP", "El Paso, TX");
    names.put("EUG", "Eugene, OR");
    names.put("EVV", "Evansville, IN");
    names.put("EWR", "Newark, NJ");
    names.put("EYW", "Key West, FL");
    names.put("FAI", "Fairbanks, AK");
    names.put("FAT", "Fresno, CA");
    names.put("FLL", "Ft. Lauderdale, FL");
    names.put("FLO", "Florence, SC");
    names.put("FNT", "Flint, MI");
    names.put("FSD", "Sioux Falls, SD");
    names.put("FSM", "Fort Smith, AR");
    names.put("FWA", "Fort Wayne, IN");
    names.put("FYV", "Fayetteville, AR");
    names.put("GEG", "Spokane, WA");
    names.put("GJT", "Grand Junction, CO");
    names.put("GRB", "Green Bay, WI");
    names.put("GRR", "Grand Rapids, MI");
    names.put("GSO", "Greensboro, NC");
    names.put("GSP", "Greeneville/Spartanburg, SC");
    names.put("GUC", "Gunnison, CO");
    names.put("HLN", "Helena, MT");
    names.put("HNL", "Honolulu, HI");
    names.put("HOU", "Houston, TX (Hobby)");
    names.put("HPN", "Westchester County, NY");
    names.put("HRL", "Harlingen, TX");
    names.put("HSV", "Huntsville, AL");
    names.put("HVN", "New Haven, CT");
    names.put("IAD", "Washington DC (Dulles)");
    names.put("IAH", "Houston, TX (Intercontinental)");
    names.put("ICT", "Wichita, KS");
    names.put("ILM", "Wilmington, NC");
    names.put("IND", "Indianapolis, IN");
    names.put("ISP", "Islip, NY");
    names.put("ITH", "Ithaca, NY");
    names.put("JAC", "Jackson Hole, WY");
    names.put("JAX", "Jacksonville, FL");
    names.put("JAN", "Jackson, MS");
    names.put("JFK", "New York, NY (Kennedy)");
    names.put("LAN", "Lansing, MI");
    names.put("LAS", "Las Vegas, NV");
    names.put("LAX", "Los Angeles, CA");
    names.put("LBB", "Lubbock, TX");
    names.put("LCH", "Lack Charles, LA");
    names.put("LEX", "Lexington, KY");
    names.put("LFT", "Lafayette, LA");
    names.put("LGA", "New York, NY (La Guardia)");
    names.put("LGB", "Long Beach, CA");
    names.put("LIH", "Lihue, Kauai, HI");
    names.put("LIT", "Little Rock, AR");
    names.put("LNK", "Lincoln, NE");
    names.put("LSE", "La Crosse, WI");
    names.put("MAF", "Odessa (Midland)");
    names.put("MCI", "Kansas city, MO");
    names.put("MCO", "Orlando, FL");
    names.put("MDT", "Harrisburg, PA");
    names.put("MDW", "Chicago, IL (Midway)");
    names.put("MEM", "Memphis, TN");
    names.put("MFE", "Mcallen, TX");
    names.put("MFR", "Medford, OR");
    names.put("MGM", "Montgomery, AL");
    names.put("MHT", "Manchester, NH");
    names.put("MIA", "Miami, FL");
    names.put("MKE", "Milwaukee, WI");
    names.put("MKG", "Muskegon, MI");
    names.put("MKK", "Molokai, HI");
    names.put("MLB", "Melbourne, FL");
    names.put("MLI", "Moline, IL");
    names.put("MOB", "Mobile, AL");
    names.put("MQT", "Marquette, MI");
    names.put("MRY", "Monterey, CA");
    names.put("MSN", "Madison, WI");
    names.put("MSP", "Minneapolis, MN");
    names.put("MSY", "New Orleans, LA");
    names.put("MYR", "Myrtle Beach, SC");
    names.put("OAK", "Oakland, CA");
    names.put("OGG", "Maui, HI");
    names.put("OKC", "Oklahoma City, OK");
    names.put("OMA", "Omaha, NE");
    names.put("ONT", "Ontario, CA");
    names.put("ORD", "Chicago, IL (O'Hare)");
    names.put("ORF", "Norfolk, VA");
    names.put("OWB", "Owensboro, KY");
    names.put("OXR", "Oxnard, CA");
    names.put("PBI", "West Palm Beach, FL");
    names.put("PDX", "Portland, OR");
    names.put("PHL", "Philadelphia, PA");
    names.put("PHX", "Phoenix, AZ");
    names.put("PIA", "Peoria, IL");
    names.put("PIT", "Pittsburgh, PA");
    names.put("PNS", "Pensacola, FL");
    names.put("PSC", "Pasco, WA");
    names.put("PSP", "Palm Springs, CA");
    names.put("PVD", "Providence, RI");
    names.put("PWM", "Portland, ME");
    names.put("RDU", "Raleigh-Durham, NC");
    names.put("RIC", "Richmond, VA");
    names.put("RNO", "Reno, NV");
    names.put("ROA", "Roanoke, VA");
    names.put("ROC", "Rochester, NY");
    names.put("RST", "Rochester, MN");
    names.put("RSW", "Ft. Myers, FL");
    names.put("SAN", "San Diego, CA");
    names.put("SAT", "San Antonio, TX");
    names.put("SAV", "Savannah, GA");
    names.put("SBA", "Santa Barbara, CA");
    names.put("SBN", "South Bend, IN");
    names.put("SDF", "Louisville, KY");
    names.put("SEA", "Seattle, WA");
    names.put("SFO", "San Francisco, CA");
    names.put("SGF", "Springfield, MO");
    names.put("SHV", "Shreveport, LA");
    names.put("SJC", "San Jose, CA");
    names.put("SJT", "San Angelo, TX");
    names.put("SJU", "San Juan, PR");
    names.put("SLC", "Salt Lake City, UT");
    names.put("SMF", "Sacramento, CA");
    names.put("SNA", "Orange County, CA");
    names.put("SPI", "Springfield, IL");
    names.put("SRQ", "Sarasota, FL");
    names.put("STL", "St. Louis, MO");
    names.put("SVS", "Wichita Falls, TX");
    names.put("SWF", "Newburgh/Steward Field, NY");
    names.put("SYR", "Syracuse, NY");
    names.put("THL", "Tallahassee, FL");
    names.put("TOL", "Toledo, OH");
    names.put("TPA", "Tampa, FL");
    names.put("TRI", "Tri-city, TN");
    names.put("TUL", "Tulsa, OK");
    names.put("TUS", "Tucson, AZ");
    names.put("TVC", "Traverse city, MI");
    names.put("TYS", "Knoxville, TN");
    names.put("VPS", "Fort Walton Beach, FL");

    names.put("ACA", "Acapulco, Mexico");
    names.put("AKL", "Auckland , New Zealand");
    names.put("AMM", "Amman , Jordan");
    names.put("AMS", "Amsterdam, Netherlands");
    names.put("ARN", "Stockholm , Sweden");
    names.put("ASU", "Asuncion, Paraguay");
    names.put("ATH", "Athens, Greece");
    names.put("AUH", "Abu Dhabi, United Arab Emirates");
    names.put("BAH", "Bahrain, Bahrain");
    names.put("BCN", "Barcelona, Spain");
    names.put("BER", "Berlin, Germany");
    names.put("BFS", "Belfast, Northern Ireland, United Kingdom");
    names.put("BIM", "Bimini, Bahamas");
    names.put("BJX", "Leon, Mexico");
    names.put("BJY", "Belgrade, Yugoslavia");
    names.put("BKK", "Bangkok, Thailand");
    names.put("BOG", "Bogota, Columbia");
    names.put("BOM", "Bombay, India");
    names.put("BRU", "Brussels, Belgium");
    names.put("BUD", "Budapest, Hungary");
    names.put("BZE", "Belize city, Belize");
    names.put("CCS", "Caracas, Venezuela");
    names.put("CDG", "Paris (Charles de Gaulle), FRANCE");
    names.put("CGK", "Jakarta, Indonesia");
    names.put("CGN", "Bonn, Germany");
    names.put("CMB", "Colombo, Sri Lanka");
    names.put("CMN", "Casablanca, Morocco");
    names.put("CPT", "Cape town , South Africa");
    names.put("CTX", "Sapporo, Japan");
    names.put("CUN", "Cancun, Mexico");
    names.put("CZM", "Cozumel, Mexico");
    names.put("DOM", "Dominica, Dominica");
    names.put("DUB", "Dublin, Ireland");
    names.put("DUS", "Dusseldorf, Germany");
    names.put("EIS", "Tortola, British Virgin Islands");
    names.put("EZE", "Buenos Aires, Argentina");
    names.put("FCO", "Rome (Leonardo da Vinci/Fiumicino), Italy");
    names.put("FRA", "Frankfurt, Germany");
    names.put("GDL", "Guadalajara, Mexico");
    names.put("GHB", "Governors harbour, Bahamas");
    names.put("GIG", "Rio de Janeiro, RJ, Brazil");
    names.put("GLA", "Glasgow, United Kingdom");
    names.put("GOT", "Gothenburg, Sweden");
    names.put("GRU", "Sao Paulo, SP, Brazil");
    names.put("GUA", "Guatemala City, Guatemala");
    names.put("GUM", "Guam");
    names.put("GVA", "Geneva, Switzerland");
    names.put("GYE", "Guayaquil, Ecuador");
    names.put("HAM", "Hamburg, Germany");
    names.put("HAN", "Hanoi, Vietnam");
    names.put("HEL", "Helsinki, Finland");
    names.put("HIW", "Hiroshima, Japan");
    names.put("HKG", "Hong Kong, Hong Kong");
    names.put("HUX", "Huatulco, Mexico");
    names.put("IST", "Istanbul, Turkey");
    names.put("JNB", "Johannesburg, South Africa");
    names.put("KBP", "Kiev, Ukraine");
    names.put("KIN", "Kingston, Jamaica");
    names.put("KIX", "Osaka, Japan");
    names.put("LED", "St. Petersburg, Russia");
    names.put("LGW", "London (Gatwick), United Kingdom");
    names.put("LHR", "London (Heathrow), United Kingdom");
    names.put("LIM", "Lima, Peru");
    names.put("LIS", "Lisbon, Portugal");
    names.put("LYN", "Lyon, France");
    names.put("MAD", "Madrid, Spain");
    names.put("MGA", "Managua, Nicaragua");
    names.put("MAN", "Manchester, England");
    names.put("MEL", "Melbourne, Victoria, Australia");
    names.put("MEX", "Mexico city, Mexico");
    names.put("MNL", "Manila, Philippines");
    names.put("MTY", "Monterrey, Mexico");
    names.put("MUC", "Munich, Germany");
    names.put("MXP", "Milan (Malpensa), Italy");
    names.put("MZT", "Mazatlan, Mexico");
    names.put("NAP", "Naples, Italy");
    names.put("NAS", "Nassau, Bahamas");
    names.put("NBO", "Nairobi, Kenya");
    names.put("NCE", "Nice, France");
    names.put("NGO", "Nagoya, Japan");
    names.put("NRT", "Tokyo (Narita), Japan");
    names.put("ORY", "Paris (Orly), France");
    names.put("OSL", "Oslo, Norway");
    names.put("OTP", "Bucharest, Romania");
    names.put("PEK", "Beijing, China");
    names.put("PID", "Paradise Island, Bahamas");
    names.put("PRG", "Prague, Czech Republic");
    names.put("PSE", "Ponce, Puerto Rico");
    names.put("PTY", "Panama city, Panama");
    names.put("PVR", "Puerto Vallarta, Mexico");
    names.put("RKV", "Reykjavik, Iceland");
    names.put("SCL", "Santiago de Chile, Chile");
    names.put("SDQ", "Santo Domingo, Dominican Republic");
    names.put("SEL", "Seoul, Korea");
    names.put("SIN", "Singapore, Singapore");
    names.put("SJD", "Los cabos, Mexico");
    names.put("SLU", "St. Lucia, St. Lucia");
    names.put("SNN", "Shannon, Ireland");
    names.put("STR", "Stuttgart, Germany");
    names.put("STX", "St. Croix, U.S. Virgin Islands");
    names.put("SVO", "Moscow (Sheremetyevo), Russia");
    names.put("SXM", "St. Maarten, Netherlands Antilles");
    names.put("SYD", "Sydney, Australia");
    names.put("TLV", "Tel Aviv, Israel");
    names.put("TPE", "Taipei, Taiwan");
    names.put("VIE", "Vienna, Austria");
    names.put("WAW", "Warsaw, Poland");
    names.put("XIY", "Xian Xianyang, China");
    names.put("YHZ", "Halifax, NS, Canada");
    names.put("YOW", "Ottawa, Canada");
    names.put("YQB", "Quebec, Canada");
    names.put("YUL", "Montreal, Canada");
    names.put("YVR", "Vancouver, BC, Canada");
    names.put("YYC", "Calgary, Canada");
    names.put("YYZ", "Toronto, Canada");
    names.put("ZIH", "Ixtapa, Mexico");
    names.put("ZRH", "Zurich, Switzerland");

    this.names = Collections.unmodifiableMap(names);
  }

  /////////////////////////  Static Methods  ////////////////////////

  /**
   * Returns the name of an airport with a given three-letter code or
   * <code>null</code> if no airport with <code>code</code> exists.
   */
  public static String getName(String code) {
    if (airportNames == null) {
      airportNames = new AirportNames();
    }

    return airportNames.names.get(code);
  }

  /**
   * Returns an unmodifiable <code>Map</code> that maps airport codes
   * to their names.
   *
   * @since Fall 2004
   * @return a map of airport codes to their names
   */
  public static Map<String, String> getNamesMap() {
    if (airportNames == null) {
      airportNames = new AirportNames();
    }

    return airportNames.names;
  }

  /**
   * Prints the names of the airports with the given codes to standard out
   * @param args Airport codes
   */
  public static void main(String[] args) {
    for ( String arg : args ) {
      String name = AirportNames.getName( arg );
      System.out.println( arg + ": " + name );
    }
  }

}
