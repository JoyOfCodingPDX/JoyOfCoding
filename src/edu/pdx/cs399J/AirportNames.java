package edu.pdx.cs410J;

import java.util.Map;

/**
 * This class is used to get the name of airport from its three-letter
 * code.
 */
public class AirportNames {

  private Map names;  // Maps three-letter code to airport name
  private static AirportNames airportNames = null;

  /**
   * Creates a new <code>AirportNames</code> and fills in all of the
   * names.
   */
  private AirportNames() {
    this.names = new java.util.HashMap();

    this.names.put("ABE", "Allentown, PA");
    this.names.put("ABQ", "Albuquerque, NM");
    this.names.put("ABI", "Abilene, TX");
    this.names.put("ACT", "Waco, TX");
    this.names.put("ALB", "Albany, NY");
    this.names.put("AMA", "Amarillo, TX");
    this.names.put("ANC", "Anchorage, AK");
    this.names.put("ATL", "Atlanta, GA");
    this.names.put("ATW", "Appleton, WI");
    this.names.put("AUS", "Austin, TX");
    this.names.put("AVP", "Wilkes Barre, PA");
    this.names.put("AZO", "Kalamazoo, MI");
    this.names.put("BDL", "Hartford, CT");
    this.names.put("BFL", "Bakersfield, CA");
    this.names.put("BGR", "Bangor, ME");
    this.names.put("BHM", "Birmingham, AL");
    this.names.put("BIL", "Billings, MT");
    this.names.put("BMI", "Bloomington, IL");
    this.names.put("BNA", "Nashville, TN");
    this.names.put("BOI", "Boise, ID");
    this.names.put("BOS", "Boston, MA");
    this.names.put("BPT", "Beaumont/Port Arthur, TX");
    this.names.put("BTR", "Batton Rouge, LA");
    this.names.put("BTV", "Burlington, VT");
    this.names.put("BUF", "Buffalo, NY");
    this.names.put("BUR", "Burbank, CA");
    this.names.put("BWI", "Baltimore, MD");
    this.names.put("CAE", "Columbia, SC");
    this.names.put("CAK", "Akron/Canton, OH");
    this.names.put("CHA", "Chattanooga, TN");
    this.names.put("CHS", "Charleston, SC");
    this.names.put("CID", "Cedar rapids, IA");
    this.names.put("CLE", "Cleveland, OH");
    this.names.put("CLT", "Charlotte, NC");
    this.names.put("CMH", "Columbus, OH");
    this.names.put("CMI", "Champaign/Urbana, IL");
    this.names.put("COS", "Colorado Springs, CO");
    this.names.put("CRP", "Corpus Christi, TX");
    this.names.put("CVG", "Cincinnati, OH");
    this.names.put("CWA", "Wausau/Stevens Point, WI");
    this.names.put("DAY", "Dayton, OH");
    this.names.put("DBQ", "Dubuque, IA");
    this.names.put("DCA", "Washington DC (National)");
    this.names.put("DEN", "Denver, CO");
    this.names.put("DET", "Detroit, MI (City)");
    this.names.put("DFW", "Dallas/Ft. Worth, TX");
    this.names.put("DRO", "Durango, CO");
    this.names.put("DSM", "Des Moines, IA");
    this.names.put("DTW", "Detroit, MI (Metro)");
    this.names.put("DUT", "Dutch Harbor, AK");
    this.names.put("EGE", "Vail, CO");
    this.names.put("ELP", "El Paso, TX");
    this.names.put("EUG", "Eugene, OR");
    this.names.put("EVV", "Evansville, IN");
    this.names.put("EWR", "Newark, NJ");
    this.names.put("EYW", "Key West, FL");
    this.names.put("FAI", "Fairbanks, AK");
    this.names.put("FAT", "Fresno, CA");
    this.names.put("FLL", "Ft. Lauderdale, FL");
    this.names.put("FLO", "Florence, SC");
    this.names.put("FNT", "Flint, MI");
    this.names.put("FSD", "Sioux Falls, SD");
    this.names.put("FSM", "Fort Smith, AR");
    this.names.put("FWA", "Fort Wayne, IN");
    this.names.put("FYV", "Fayetteville, AR");
    this.names.put("GEG", "Spokane, WA");
    this.names.put("GJT", "Grand Junction, CO");
    this.names.put("GRB", "Green Bay, WI");
    this.names.put("GRR", "Grand Rapids, MI");
    this.names.put("GSO", "Greensboro, NC");
    this.names.put("GSP", "Greeneville/Spartanburg, SC");
    this.names.put("GUC", "Gunnison, CO");
    this.names.put("HLN", "Helena, MT");
    this.names.put("HNL", "Honolulu, HI");
    this.names.put("HOU", "Houston, TX (Hobby)");
    this.names.put("HPN", "Westchester County, NY");
    this.names.put("HRL", "Harlingen, TX");
    this.names.put("HSV", "Huntsville, AL");
    this.names.put("HVN", "New Haven, CT");
    this.names.put("IAD", "Washington DC (Dulles)");
    this.names.put("IAH", "Houston, TX (Intercontinental)");
    this.names.put("ICT", "Wichita, KS");
    this.names.put("ILM", "Wilmington, NC");
    this.names.put("IND", "Indianapolis, IN");
    this.names.put("ISP", "Islip, NY");
    this.names.put("ITH", "Ithaca, NY");
    this.names.put("JAC", "Jackson Hole, WY");
    this.names.put("JAX", "Jacksonville, FL");
    this.names.put("JAN", "Jackson, MS");
    this.names.put("JFK", "New York, NY (Kennedy)");
    this.names.put("LAN", "Lansing, MI");
    this.names.put("LAS", "Las Vegas, NV");
    this.names.put("LAX", "Los Angeles, CA");
    this.names.put("LBB", "Lubbock, TX");
    this.names.put("LCH", "Lack Charles, LA");
    this.names.put("LEX", "Lexington, KY");
    this.names.put("LFT", "Lafayette, LA");
    this.names.put("LGA", "New York, NY (La Guardia)");
    this.names.put("LGB", "Long Beach, CA");
    this.names.put("LIH", "Lihue, Kauai, HI");
    this.names.put("LIT", "Little Rock, AR");
    this.names.put("LNK", "Lincoln, NE");
    this.names.put("LSE", "La Crosse, WI");
    this.names.put("MAF", "Odessa (Midland)");
    this.names.put("MCI", "Kansas city, MO");
    this.names.put("MCO", "Orlando, FL");
    this.names.put("MDT", "Harrisburg, PA");
    this.names.put("MDW", "Chicago, IL (Midway)");
    this.names.put("MEM", "Memphis, TN");
    this.names.put("MFE", "Mcallen, TX");
    this.names.put("MFR", "Medford, OR");
    this.names.put("MGM", "Montgomery, AL");
    this.names.put("MHT", "Manchester, NH");
    this.names.put("MIA", "Miami, FL");
    this.names.put("MKE", "Milwaukee, WI");
    this.names.put("MKG", "Muskegon, MI");
    this.names.put("MKK", "Molokai, HI");
    this.names.put("MLB", "Melbourne, FL");
    this.names.put("MLI", "Moline, IL");
    this.names.put("MOB", "Mobile, AL");
    this.names.put("MQT", "Marquette, MI");
    this.names.put("MRY", "Monterey, CA");
    this.names.put("MSN", "Madison, WI");
    this.names.put("MSP", "Minneapolis, MN");
    this.names.put("MSY", "New Orleans, LA");
    this.names.put("MYR", "Myrtle Beach, SC");
    this.names.put("OAK", "Oakland, CA");
    this.names.put("OGG", "Maui, HI");
    this.names.put("OKC", "Oklahoma City, OK");
    this.names.put("OMA", "Omaha, NE");
    this.names.put("ONT", "Ontario, CA");
    this.names.put("ORD", "Chicago, IL (O'Hare)");
    this.names.put("ORF", "Norfolk, VA");
    this.names.put("OWB", "Owensboro, KY");
    this.names.put("OXR", "Oxnard, CA");
    this.names.put("PBI", "West Palm Beach, FL");
    this.names.put("PDX", "Portland, OR");
    this.names.put("PHL", "Philadelphia, PA");
    this.names.put("PHX", "Phoenix, AZ");
    this.names.put("PIA", "Peoria, IL");
    this.names.put("PIT", "Pittsburgh, PA");
    this.names.put("PNS", "Pensacola, FL");
    this.names.put("PSC", "Pasco, WA");
    this.names.put("PSP", "Palm Springs, CA");
    this.names.put("PVD", "Providence, RI");
    this.names.put("PWM", "Portland, ME");
    this.names.put("RDU", "Raleigh-Durham, NC");
    this.names.put("RIC", "Richmond, VA");
    this.names.put("RNO", "Reno, NV");
    this.names.put("ROA", "Roanoke, VA");
    this.names.put("ROC", "Rochester, NY");
    this.names.put("RST", "Rochester, MN");
    this.names.put("RSW", "Ft. Myers, FL");
    this.names.put("SAN", "San Diego, CA");
    this.names.put("SAT", "San Antonio, TX");
    this.names.put("SAV", "Savannah, GA");
    this.names.put("SBA", "Santa Barbara, CA");
    this.names.put("SBN", "South Bend, IN");
    this.names.put("SDF", "Louisville, KY");
    this.names.put("SEA", "Seattle, WA");
    this.names.put("SFO", "San Francisco, CA");
    this.names.put("SGF", "Springfield, MO");
    this.names.put("SHV", "Shreveport, LA");
    this.names.put("SJC", "San Jose, CA");
    this.names.put("SJT", "San Angelo, TX");
    this.names.put("SJU", "San Juan, PR");
    this.names.put("SLC", "Salt Lake City, UT");
    this.names.put("SMF", "Sacramento, CA");
    this.names.put("SNA", "Orange County, CA");
    this.names.put("SPI", "Springfield, IL");
    this.names.put("SRQ", "Sarasota, FL");
    this.names.put("STL", "St. Louis, MO");
    this.names.put("SVS", "Wichita Falls, TX");
    this.names.put("SWF", "Newburgh/Steward Field, NY");
    this.names.put("SYR", "Syracuse, NY");
    this.names.put("THL", "Tallahassee, FL");
    this.names.put("TOL", "Toledo, OH");
    this.names.put("TPA", "Tampa, FL");
    this.names.put("TRI", "Tri-city, TN");
    this.names.put("TUL", "Tulsa, OK");
    this.names.put("TUS", "Tucson, AZ");
    this.names.put("TVC", "Traverse city, MI");
    this.names.put("TYS", "Knoxville, TN");
    this.names.put("VPS", "Fort Walton Beach, FL");

    this.names.put("ACA", "Acapulco, Mexico");
    this.names.put("AKL", "Auckland , New Zealand");
    this.names.put("AMM", "Amman , Jordan");
    this.names.put("AMS", "Amsterdam, Netherlands");
    this.names.put("ARN", "Stockholm , Sweden");
    this.names.put("ASU", "Asuncion, Paraguay");
    this.names.put("ATH", "Athens, Greece");
    this.names.put("AUH", "Abu Dhabi, United Arab Emirates");
    this.names.put("BAH", "Bahrain, Bahrain");
    this.names.put("BCN", "Barcelona, Spain");
    this.names.put("BER", "Berlin, Germany");
    this.names.put("BFS", "Belfast, Northern Ireland, United Kingdom");
    this.names.put("BIM", "Bimini, Bahamas");
    this.names.put("BJX", "Leon, Mexico");
    this.names.put("BJY", "Belgrade, Yugoslavia");
    this.names.put("BKK", "Bangkok, Thailand");
    this.names.put("BOG", "Bogota, Columbia");
    this.names.put("BOM", "Bombay, India");
    this.names.put("BRU", "Brussels, Belgium");
    this.names.put("BUD", "Budapest, Hungary");
    this.names.put("BZE", "Belize city, Belize");
    this.names.put("CCS", "Caracas, Venezuela");
    this.names.put("CDG", "Paris (Charles de Gaulle), FRANCE");
    this.names.put("CGK", "Jakarta, Indonesia");
    this.names.put("CGN", "Bonn, Germany");
    this.names.put("CMB", "Colombo, Sri Lanka");
    this.names.put("CMN", "Casablanca, Morocco");
    this.names.put("CPT", "Cape town , South Africa");
    this.names.put("CTX", "Sapporo, Japan");
    this.names.put("CUN", "Cancun, Mexico");
    this.names.put("CZM", "Cozumel, Mexico");
    this.names.put("DOM", "Dominica, Dominica");
    this.names.put("DUB", "Dublin, Ireland");
    this.names.put("DUS", "Dusseldorf, Germany");
    this.names.put("EIS", "Tortola, British Virgin Islands");
    this.names.put("EZE", "Buenos Aires, Argentina");
    this.names.put("FCO", "Rome (Leonardo da Vinci/Fiumicino), Italy");
    this.names.put("FRA", "Frankfurt, Germany");
    this.names.put("GDL", "Guadalajara, Mexico");
    this.names.put("GHB", "Governors harbour, Bahamas");
    this.names.put("GIG", "Rio de Janeiro, RJ, Brazil");
    this.names.put("GLA", "Glasgow, United Kingdom");
    this.names.put("GOT", "Gothenburg, Sweden");
    this.names.put("GRU", "Sao Paulo, SP, Brazil");
    this.names.put("GUA", "Guatemala City, Guatemala");
    this.names.put("GUM", "Guam");
    this.names.put("GVA", "Geneva, Switzerland");
    this.names.put("GYE", "Guayaquil, Ecuador");
    this.names.put("HAM", "Hamburg, Germany");
    this.names.put("HAN", "Hanoi, Vietnam");
    this.names.put("HEL", "Helsinki, Finland");
    this.names.put("HIW", "Hiroshima, Japan");
    this.names.put("HKG", "Hong Kong, Hong Kong");
    this.names.put("HUX", "Huatulco, Mexico");
    this.names.put("IST", "Istanbul, Turkey");
    this.names.put("JNB", "Johannesburg, South Africa");
    this.names.put("KBP", "Kiev, Ukraine");
    this.names.put("KIN", "Kingston, Jamaica");
    this.names.put("KIX", "Osaka, Japan");
    this.names.put("LED", "St. Petersburg, Russia");
    this.names.put("LGW", "London (Gatwick), United Kingdom");
    this.names.put("LHR", "London (Heathrow), United Kingdom");
    this.names.put("LIM", "Lima, Peru");
    this.names.put("LIS", "Lisbon, Portugal");
    this.names.put("LYN", "Lyon, France");
    this.names.put("MAD", "Madrid, Spain");
    this.names.put("MGA", "Managua, Nicaragua");
    this.names.put("MAN", "Manchester, England");
    this.names.put("MEL", "Melbourne, Victoria, Australia");
    this.names.put("MEX", "Mexico city, Mexico");
    this.names.put("MNL", "Manila, Philippines");
    this.names.put("MTY", "Monterrey, Mexico");
    this.names.put("MUC", "Munich, Germany");
    this.names.put("MXP", "Milan (Malpensa), Italy");
    this.names.put("MZT", "Mazatlan, Mexico");
    this.names.put("NAP", "Naples, Italy");
    this.names.put("NAS", "Nassau, Bahamas");
    this.names.put("NBO", "Nairobi, Kenya");
    this.names.put("NCE", "Nice, France");
    this.names.put("NGO", "Nagoya, Japan");
    this.names.put("NRT", "Tokyo (Narita), Japan");
    this.names.put("ORY", "Paris (Orly), France");
    this.names.put("OSL", "Oslo, Norway");
    this.names.put("OTP", "Bucharest, Romania");
    this.names.put("PEK", "Beijing, China");
    this.names.put("PID", "Paradise Island, Bahamas");
    this.names.put("PRG", "Prague, Czech Republic");
    this.names.put("PSE", "Ponce, Puerto Rico");
    this.names.put("PTY", "Panama city, Panama");
    this.names.put("PVR", "Puerto Vallarta, Mexico");
    this.names.put("RKV", "Reykjavik, Iceland");
    this.names.put("SCL", "Santiago de Chile, Chile");
    this.names.put("SDQ", "Santo Domingo, Dominican Republic");
    this.names.put("SEL", "Seoul, Korea");
    this.names.put("SIN", "Singapore, Singapore");
    this.names.put("SJD", "Los cabos, Mexico");
    this.names.put("SLU", "St. Lucia, St. Lucia");
    this.names.put("SNN", "Shannon, Ireland");
    this.names.put("STR", "Stuttgart, Germany");
    this.names.put("STX", "St. Croix, U.S. Virgin Islands");
    this.names.put("SVO", "Moscow (Sheremetyevo), Russia");
    this.names.put("SXM", "St. Maarten, Netherlands Antilles");
    this.names.put("SYD", "Sydney, Australia");
    this.names.put("TLV", "Tel Aviv, Israel");
    this.names.put("TPE", "Taipei, Taiwan");
    this.names.put("VIE", "Vienna, Austria");
    this.names.put("WAW", "Warsaw, Poland");
    this.names.put("XIY", "Xian Xianyang, China");
    this.names.put("YHZ", "Halifax, NS, Canada");
    this.names.put("YOW", "Ottawa, Canada");
    this.names.put("YQB", "Quebec, Canada");
    this.names.put("YUL", "Montreal, Canada");
    this.names.put("YVR", "Vancouver, BC, Canada");
    this.names.put("YYC", "Calgary, Canada");
    this.names.put("YYZ", "Toronto, Canada");
    this.names.put("ZIH", "Ixtapa, Mexico");
    this.names.put("ZRH", "Zurich, Switzerland");
  }

  /**
   * Returns the name of an airport with a given three-letter code.
   */
  public static String getName(String code) {
    if(airportNames == null) {
      airportNames = new AirportNames();
    }

    return((String) airportNames.names.get(code));
  }

  /**
   * Quick test program
   */
  public static void main(String[] args) {
    for(int i = 0; i < args.length; i++) {
      String name = AirportNames.getName(args[i]);
      System.out.println(args[i] + ": " + name);
    }
  }

}
