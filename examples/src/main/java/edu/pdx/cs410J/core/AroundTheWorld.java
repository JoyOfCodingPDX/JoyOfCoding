package edu.pdx.cs410J.core;

import java.io.*;
import java.text.*;
import java.util.*;

/**
 * This class shows off some of Java's internationalization (i18n)
 * capabilities using the <code>Locale</code> class.
 */
public class AroundTheWorld {

  private static PrintWriter out = new PrintWriter(System.out, true);
  private static PrintWriter err = new PrintWriter(System.err, true);

  private static void usage() {
    err.println("usage: java AroundTheWorld [options]");
    err.println("  -country code      Which country?");
    err.println("  -language code     Which language?");
    err.println("  -timeZone code     Which time zone?");
    err.println("If any code is ??, then all possible values for " +
		"that code are printed");
    System.exit(1);
  }

  /**
   * Prints the available locales formatted using a given locale.
   */
  private static void printAvailableLocales(Locale locale) {
    Locale[] locales = Locale.getAvailableLocales();
    out.println("Available locales:");
    for (int i = 0; i < locales.length; i++) {
      Locale l = locales[i];
      out.println("Locale " + i + " of " + locales.length);
      out.println("  Name: " + l.getDisplayName(locale));
      out.println("  Country: " + l.getDisplayCountry(locale) + " (" +
		  l.getCountry() + ")");
      out.println("  Language: " + l.getDisplayLanguage(locale) + 
		  " (" + l.getLanguage() + ")");
      out.println("  Variant: " + l.getDisplayVariant(locale) + " (" +
		  l.getVariant() + ")");
    }
  }

  /**
   * Prints the codes for all available time zones formatted for a
   * given locale.
   */
  private static void printTimeZones(Locale locale) {
    String[] codes = TimeZone.getAvailableIDs();
    out.println("Time zones");
    for (int i = 0; i < codes.length; i++) {
      String code = codes[i];
      TimeZone tz = TimeZone.getTimeZone(code);
      out.println("  " + code + ": " + tz.getDisplayName(locale));
    }
  }

  /**
   * Prints out today's date and time in a specific locale.
   */
  private static void printToday(Locale locale, TimeZone tz) {
    DateFormat df = 
      DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL,
				     locale);
    df.setTimeZone(tz);
    out.println("Right now: " + df.format(new Date()));
  }

  /**
   * Prints out a number in various locale-specific formats
   */
  private static void printNumber(double number, Locale locale) {
    NumberFormat nf;

    nf = NumberFormat.getNumberInstance(locale);
    out.println("A number: " + nf.format(number));

    nf = NumberFormat.getCurrencyInstance(locale);
    out.println("Currency: " + nf.format(number));

    nf = NumberFormat.getPercentInstance(locale);
    out.println("Percent: " + nf.format(number));
  }

  /**
   * Parses the command line to determine which locale to display
   * information in.  Prints out dates, times, and currencies using
   * various locales.
   */
  public static void main(String[] args) {
    String countryCode = null;
    String languageCode = null;
    String timeZoneCode = null;

    // Parse the command line
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-country")) {
	if(++i >= args.length) {
	  err.println("** Missing country code");
	  usage();
	}

	countryCode = args[i];

      } else if (args[i].equals("-language")) {
	if(++i >= args.length) {
	  err.println("** Missing language code");
	  usage();
	}

	languageCode = args[i];

      } else if (args[i].equals("-timeZone")) {
	if(++i >= args.length) {
	  err.println("** Missing time zone code");
	  usage();
	}

	timeZoneCode = args[i];

      } else {
	err.println("** Unknown option: " + args[i]);
	usage();
      }
    }

    Locale locale;

    if (countryCode == null) {
      // Use default
      locale = Locale.getDefault();

    } else if (countryCode.equals("??")) {
      locale = Locale.getDefault();
      printAvailableLocales(locale);

    } else if (languageCode == null) {
      // Use default
      locale = Locale.getDefault();

    } else if (languageCode.equals("??")) {
      locale = Locale.getDefault();
      printAvailableLocales(locale);

    } else {
      locale = new Locale(languageCode, countryCode);
    }

    TimeZone timeZone;
    if (timeZoneCode == null) {
      // Use default
      timeZone = TimeZone.getDefault();

    } else if (timeZoneCode.equals("??")) {
      timeZone = TimeZone.getDefault();
      printTimeZones(locale);

    } else {
      timeZone = TimeZone.getTimeZone(timeZoneCode);
    }

    // Print out some interesting info
    out.println("");
    printToday(locale, timeZone);
    printNumber(1234.56, locale);
    out.println("");

  }

}
