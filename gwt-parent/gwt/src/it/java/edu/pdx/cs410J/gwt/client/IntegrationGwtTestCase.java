package edu.pdx.cs410J.gwt.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * The abstract superclass of all integration tests
 */
public abstract class IntegrationGwtTestCase extends GWTTestCase {
  @Override
  public String getModuleName() {
    return "edu.pdx.cs410J.gwt.IntegrationTests";
  }
}
