package edu.pdx.cs410J.di;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

/**
 * The superclass of classes that store objects serialized to a file using JAXB
 */
public abstract class JaxbDatabase
{
    private final File directory;
    private final String fileName;
    private final JAXBContext xmlContext;
    private final File file;

    protected JaxbDatabase( File directory, String fileName, Class<?>... jaxbClasses )
        throws IOException, JAXBException
    {
        this.directory = directory;
        this.fileName = fileName;
        this.xmlContext = JAXBContext.newInstance( jaxbClasses );

        this.directory.mkdirs();
        if (!this.directory.exists()) {
          throw new IOException( "Could not create data directory: " + this.directory);
        }


        this.file = new File(this.directory, this.fileName);
    }

    /**
     * Writes an object as XML to the data file
     * @param xml The object to marshal
     */
    protected void writeXml( Object xml )
    {
        try
        {
            Marshaller marshaller = this.xmlContext.createMarshaller();
            marshaller.marshal( xml, new File(this.directory, this.fileName) );
        }
        catch ( JAXBException ex )
        {
            throw new IllegalStateException( "Could not save inventory", ex);
        }
    }

    public File getDatabaseFile() {
        return file;
    }

    /**
     * Read the XML data file and returns the unmarshalled object
     * @return The object in the XML file or null if the file doesn't exist
     * @throws JAXBException if we can't read the file
     */
    protected Object readFile()
        throws JAXBException
    {
        System.out.println("Reading xml data from " + file );

        if ( this.file.exists()) {
          Unmarshaller unmarshaller = this.xmlContext.createUnmarshaller();
          return unmarshaller.unmarshal( this.file );
        } else {
          return null;
        }
    }
}
