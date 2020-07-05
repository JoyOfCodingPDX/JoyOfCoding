#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.UncaughtExceptionInMain;
import ${package}.PhoneBillRestClient.PhoneBillRestException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the {@link Project4} class by invoking its main method with various arguments
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

    @Test
    public void test0RemoveAllMappings() throws IOException {
      PhoneBillRestClient client = new PhoneBillRestClient(HOSTNAME, Integer.parseInt(PORT));
      client.removeAllDictionaryEntries();
    }

    @Test
    public void test1NoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }

    @Test
    public void test2EmptyServer() {
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT );
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatWordCount(0)));
    }

    @Test(expected = PhoneBillRestException.class)
    public void test3NoDefinitionsThrowsAppointmentBookRestException() throws Throwable {
        String word = "WORD";
        try {
            invokeMain(Project4.class, HOSTNAME, PORT, word);

        } catch (UncaughtExceptionInMain ex) {
            throw ex.getCause();
        }
    }

    @Test
    public void test4AddDefinition() {
        String word = "WORD";
        String definition = "DEFINITION";

        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, word, definition );
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.definedWordAs(word, definition)));

        result = invokeMain( Project4.class, HOSTNAME, PORT, word );
        out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatDictionaryEntry(word, definition)));

        result = invokeMain( Project4.class, HOSTNAME, PORT );
        out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatDictionaryEntry(word, definition)));
    }
}