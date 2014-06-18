package edu.pdx.cs410J.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;

import java.util.Date;

public class DateLocalizationExample extends Example
{
    private final Label label;

    public DateLocalizationExample()
    {
        super( "Date Localization" );

        DateLocalizationStrings strings = GWT.create( DateLocalizationStrings.class );
        this.label = new Label();
        this.label.setText( strings.defaultDaysMessage() );
        this.add(new Label(strings.selectString()));

        final DateLocalizationMessages messages = GWT.create( DateLocalizationMessages.class );

        DateBox dateBox = new DateBox();
        dateBox.setFormat( new DateBox.DefaultFormat( DateTimeFormat.getLongDateFormat()) );
        Date date = new Date();
        CalendarUtil.addMonthsToDate( date, -29 * 12 );
        dateBox.setValue( date );

        dateBox.addValueChangeHandler( new ValueChangeHandler<Date>() {

            public void onValueChange( ValueChangeEvent<Date> e )
            {
              long days = CalendarUtil.getDaysBetween(e.getValue(), new Date() );
              label.setText( messages.daysMessage( days ) );
            }
        });

        this.add( dateBox );
        this.add( label );
    }
}
