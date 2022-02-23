package edu.pdx.cs410J;

import org.xml.sax.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class ProjectXmlHelper implements ErrorHandler, EntityResolver {
  private final String publicId;
  private final String systemId;
  private final String dtdFileName;

  protected ProjectXmlHelper(String publicId, String systemId, String dtdFileName) {
    this.publicId = publicId;
    this.systemId = systemId;
    this.dtdFileName = dtdFileName;
  }

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    throw exception;
  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    throw exception;
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    throw exception;
  }

  @Override
  public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
    if (this.publicId.equals(publicId) || this.systemId.equals(systemId)) {
      // We're resolving the external entity for the DTD
      // Check to see if it's in the jar file.  This way we don't
      // need to go all the way to the website to find the DTD.
      InputStream stream =
        ProjectXmlHelper.class.getResourceAsStream(this.dtdFileName);
      if (stream != null) {
        return new InputSource(stream);
      }
    }

    // Try to access the DTD using the URL
    try {
      URL url = new URL(systemId);
      InputStream stream = url.openStream();
      return new InputSource(stream);

    } catch (Exception ex) {
      return null;
    }
  }
}
