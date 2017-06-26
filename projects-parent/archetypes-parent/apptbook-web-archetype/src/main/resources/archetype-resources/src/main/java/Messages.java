#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    public static String formatMappingCount(int count )
    {
        return String.format( "Server contains %d key/value pairs", count );
    }

    public static String formatKeyValuePair( String key, String value )
    {
        return String.format("  %s -> %s", key, value);
    }

    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter ${symbol_escape}"%s${symbol_escape}" is missing", parameterName);
    }

    public static String mappedKeyValue( String key, String value )
    {
        return String.format( "Mapped %s to %s", key, value );
    }

    public static String allMappingsDeleted() {
        return "All mappings have been deleted";
    }

    public static Map.Entry<String, String> parseKeyValuePair(String content) {
        Pattern pattern = Pattern.compile("${symbol_escape}${symbol_escape}s*(.*) -> (.*)");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return null;
        }

        return new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return matcher.group(1);
            }

            @Override
            public String getValue() {
                String value = matcher.group(2);
                if ("null".equals(value)) {
                    value = null;
                }
                return value;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException("This method is not implemented yet");
            }
        };
    }

    public static void formatKeyValueMap(PrintWriter pw, Map<String, String> map) {
        pw.println(Messages.formatMappingCount(map.size()));

        for (Map.Entry<String, String> entry : map.entrySet()) {
            pw.println(Messages.formatKeyValuePair(entry.getKey(), entry.getValue()));
        }
    }

    public static Map<String, String> parseKeyValueMap(String content) {
        Map<String, String> map = new HashMap<>();

        String[] lines = content.split("${symbol_escape}n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            Map.Entry<String, String> entry = parseKeyValuePair(line);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }
}
