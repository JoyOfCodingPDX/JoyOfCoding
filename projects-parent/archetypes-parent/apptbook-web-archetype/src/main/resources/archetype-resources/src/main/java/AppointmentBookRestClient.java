#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

/**
 * A helper class for accessing the rest client
 */
public class AppointmentBookRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "apptbook";
    private static final String SERVLET = "appointments";

    private final String url;


    /**
     * Creates a client to the appointment book REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AppointmentBookRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Returns all keys and values from the server
     */
    public Response getAllKeysAndValues() throws IOException
    {
        return get(this.url );
    }

    /**
     * Returns all values for the given key
     */
    public Response getValues( String key ) throws IOException
    {
        return get(this.url, "key", key);
    }

    public Response addKeyValuePair( String key, String value ) throws IOException
    {
        return postToMyURL("key", key, "value", value);
    }

    @VisibleForTesting
    Response postToMyURL(String... keysAndValues) throws IOException {
        return post(this.url, keysAndValues);
    }

    public Response removeAllMappings() throws IOException {
        return delete(this.url);
    }
}
