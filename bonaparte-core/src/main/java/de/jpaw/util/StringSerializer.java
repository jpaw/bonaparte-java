package de.jpaw.util;

import de.jpaw.bonaparte.core.StringBuilderConstants;

/**
 * Converts a Bonaportable between a StringBuilder and a readable/editable String. It can be used to serialize bonaportables in text files while each
 * bonaportable takes a single line.
 */
public class StringSerializer extends StringBuilderConstants {

    /**
     * The escape char to display the bonaportable control chars
     */
    protected static final char ESC = '\\';

    /**
     * Converts a bonaPortable provided with a Stringbuilder to a simple string representation. All bonaportable control characters, tabs and backslashes are
     * converted to escaped chars.
     * 
     * @param builder
     *            A Stringbuilder that containes a bonaportable
     * @return the converted bonaportable
     */
    public static String toString(StringBuilder builder) {
        StringBuilder result = new StringBuilder();
        for (char c : builder.toString().toCharArray()) {
            if (c == '\t') {
                // special handling for tabs that only appear in ascii/unicode fields. We want java notation
                result.append("\\t");
            } else if (c < 32) {
                // all chars 0..31 are control chars, prefix with \ and shift value to letter space (+64)
                result.append('\\').append((char) (c + 64));
            } else if (c == '\\') {
                // escape the escape char
                result.append("\\\\");
            } else {
                // leave everything else untouched
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Converts a bonaPortable provided with a simple String representation to Stringbuilder.
     */
    public static StringBuilder fromString(String string) {
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (char c : string.toCharArray()) {
            if (escaped) {
                escaped = false;
                if (c == 't') {
                    // insert a tab
                    result.append('\t');
                } else if (c == '\\') {
                    // insert a backslash
                    result.append(c);
                } else {
                    // insert a control char
                    result.append((char) (c - 64));
                }
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        return result;
    }
}
