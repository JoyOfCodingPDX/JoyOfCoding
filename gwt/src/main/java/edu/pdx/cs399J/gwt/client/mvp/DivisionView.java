package edu.pdx.cs399J.gwt.client.mvp;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import edu.pdx.cs399J.gwt.client.Example;

/**
 * The view for the MVP implementation of the division service example.  It uses UI Binder to layout the UI.
 *
 * @author David Whitlock
 * @since Summer 2010
 */
public class DivisionView extends Example implements DivisionPresenter.Display
{
    /** The weirdo interface required by UI Binder */
    public interface Binder extends UiBinder<HorizontalPanel, DivisionView> {}

    /** Interface with CSS styles needed by this view */
    public interface DivisionStyle extends CssResource {
        String error();
    }

    @UiField
    TextBox dividend;

    @UiField
    TextBox divisor;

    @UiField
    HasValue<String> quotient;

    @UiField
    Button equals;

    @UiField
    DivisionStyle style;

    public DivisionView(Binder binder) {
        super("Division with MVP");

        add( binder.createAndBindUi( this ));
    }

    public void setDividendChangeHandler( ValueChangeHandler<String> handler )
    {
        dividend.addValueChangeHandler( handler );
    }

    public void setDividendValid( boolean valid )
    {
        if (valid) {
            dividend.removeStyleName( style.error() );

        } else {
            dividend.addStyleName( style.error() );
        }
    }

    public void setDivisionClickHandler( ClickHandler handler )
    {
       equals.addClickHandler( handler );
    }

    public void setDivisionEnabled( boolean enabled )
    {
        equals.setEnabled( enabled );
    }

    public void setDivisorChangeHandler( ValueChangeHandler<String> handler )
    {
       divisor.addValueChangeHandler( handler );
    }

    public void setDivisorValid( boolean valid )
    {
        if (valid) {
            divisor.removeStyleName( style.error() );

        } else {
            divisor.addStyleName( style.error() );
        }
    }

    public void setErrorMessage( String message )
    {
        Window.alert( message );
    }

    public void setQuotient( String quotient )
    {
        this.quotient.setValue( quotient );
    }
}
