#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package beginner;

import com.sandwich.koan.Koan;

import static com.sandwich.util.Assertions.fail;

public class AboutKoans {

    @Koan
    public void findAboutKoansFile() {
        fail("delete this line to advance");
    }

    @Koan
    public void definitionOfKoanCompletion() {
        boolean koanIsComplete = false;
        if (!koanIsComplete) {
            fail("what if koanIsComplete variable was true?");
        }
    }

}
