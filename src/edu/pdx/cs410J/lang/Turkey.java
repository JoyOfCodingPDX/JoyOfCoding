package edu.pdx.cs410J.lang;

/**
 * This class represents a Turkey.  Turkies are birds that do not fly.
 */
public class Turkey extends Bird {

  public Turkey(String name) {
    this.name = name;
  }

  public String says() {
    return "Gobble";
  }

}
