package edu.pdx.cs410J.di;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.io.File;

/**
 * A Guice module that binds in all of the REST services and filters
 */
public class RestModule extends ServletModule
{
    @Override
    protected void configureServlets()
    {
        bindRestServices();

        bind(HttpServletDispatcher.class).in( Singleton.class );
        serve( "/rest*" ).with( HttpServletDispatcher.class );
        filter( "/rest*" ).through( RestLoggingFilter.class );

        // The Logger is already bound by someone else (Resteasy, perhaps?)
        bind(File.class).annotatedWith(DataDirectory.class).toInstance(new File(System.getProperty("user.dir")));
        bind(CreditCardDatabase.class).in(Singleton.class);
    }

    private void bindRestServices()
    {
        bind( RestfulCreditCardService.class);
    }
}
