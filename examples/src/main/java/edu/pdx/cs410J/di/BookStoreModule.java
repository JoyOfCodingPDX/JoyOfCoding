package edu.pdx.cs410J.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import java.io.File;

/**
 * A Guice module that configures the well-known objects for the book store application
 */
public class BookStoreModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(BookInventory.class).to(BookDatabase.class);

        bind(CreditCardService.class).to(FirstBankOfPSU.class).in(Singleton.class);

        bind(String.class).annotatedWith( Names.named("ServerHost") ).toInstance( "localhost" );
        bind(Integer.class).annotatedWith( Names.named("ServerPort") ).toInstance( 8080 );

//        String tmpdir = System.getProperty("java.io.tmpdir");
//        File directory = new File(tmpdir);
//        bind(File.class).annotatedWith(DataDirectory.class).toInstance(directory);
    }

    @Provides
    @DataDirectory
    protected File provideDataDirectory() {
      String tmpdir = System.getProperty( "java.io.tmpdir" );
      return new File( tmpdir );
    }
}
