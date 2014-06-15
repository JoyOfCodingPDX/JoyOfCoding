package edu.pdx.cs410J.gwt.client;

import com.google.gwt.i18n.client.Messages;

public interface DateLocalizationMessages extends Messages
{
    @DefaultMessage( "You have been alive for {0,number} days")
    String daysMessage( long days );
}
