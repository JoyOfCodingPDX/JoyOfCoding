package edu.pdx.cs399J.YOU.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Date;

/**
 * A basic GWT class
 */
public class Project6 implements EntryPoint {
  public void onModuleLoad() {
    Button button = new Button("Ping Server");
    button.addClickHandler(new ClickHandler() {
        public void onClick( ClickEvent clickEvent )
        {
            PingServiceAsync async = GWT.create( PingService.class );
            async.ping( new AsyncCallback<Date>() {

                public void onFailure( Throwable ex )
                {
                    Window.alert(ex.toString());
                }

                public void onSuccess( Date date )
                {
                    Window.alert( "The time on the server is " + date );
                }
            });
        }
    });
      RootPanel rootPanel = RootPanel.get();
      rootPanel.add(button);
  }
}
