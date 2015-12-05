#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import edu.pdx.cs410J.AbstractFlight;
import edu.pdx.cs410J.AbstractAirline;

import java.util.Collection;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {
  public void onModuleLoad() {
    Button button = new Button("Ping Server");
    button.addClickHandler(new ClickHandler() {
        public void onClick( ClickEvent clickEvent )
        {
            PingServiceAsync async = GWT.create( PingService.class );
            async.ping( new AsyncCallback<AbstractAirline>() {

                public void onFailure( Throwable ex )
                {
                    Window.alert(ex.toString());
                }

                public void onSuccess( AbstractAirline airline )
                {
                    StringBuilder sb = new StringBuilder( airline.toString() );
                    Collection<AbstractFlight> flights = airline.getFlights();
                    for ( AbstractFlight flight : flights ) {
                        sb.append(flight);
                        sb.append("${symbol_escape}n");
                    }
                    Window.alert( sb.toString() );
                }
            });
        }
    });
      RootPanel rootPanel = RootPanel.get();
      rootPanel.add(button);
  }
}
