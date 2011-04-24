package edu.pdx.cs410J.gwt.client;

import com.google.gwt.i18n.client.Constants;

public interface DateLocalizationStrings extends Constants
{
    @DefaultStringValue( "Input your date of birth" )
    public String selectString();

    @DefaultStringValue( "How many days have you been alive?" )
    public String defaultDaysMessage();
}
