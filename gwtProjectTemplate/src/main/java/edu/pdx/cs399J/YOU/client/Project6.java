package edu.pdx.cs399J.YOU.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import edu.pdx.cs399J.AbstractPhoneBill;
import edu.pdx.cs399J.AbstractPhoneCall;

import java.util.Collection;

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
            async.ping( new AsyncCallback<AbstractPhoneBill>() {

                public void onFailure( Throwable ex )
                {
                    Window.alert(ex.toString());
                }

                public void onSuccess( AbstractPhoneBill bill )
                {
                    StringBuilder sb = new StringBuilder( bill.toString() );
                    Collection<AbstractPhoneCall> calls = bill.getPhoneCalls();
                    for ( AbstractPhoneCall call : calls ) {
                        sb.append(call);
                        sb.append("\n");
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
