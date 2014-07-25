package de.jpaw.util;

import java.io.IOException;

public class SockJSJsonEscaperForAppendables extends DefaultJsonEscaperForAppendables {
    public static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public SockJSJsonEscaperForAppendables(Appendable appendable) {
        super(appendable);
    }
    
    protected void writeUnicodeEscape(char c) throws IOException {
        appendable.append('\\');
        appendable.append('u');
        appendable.append(HEX_CHARS[(c >> 12) & 0xF]);
        appendable.append(HEX_CHARS[(c >> 8) & 0xF]);
        appendable.append(HEX_CHARS[(c >> 4) & 0xF]);
        appendable.append(HEX_CHARS[c & 0xF]);
    }


    /** Write the String s (which may not be null) to the Appendable.
     * This implementation may not yet be fully Unicode-compliant.
     * See here for the explanation: http://stackoverflow.com/questions/1527856/how-can-i-iterate-through-the-unicode-codepoints-of-a-java-string
     *  */ 
    @Override
    public void outputUnicodeNoControls(String s) throws IOException {
        appendable.append('\"');
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (((c) & 0x7f) != 0) {   // TODO: check if this works correctly for Unicodes characters of the upper plane
                writeUnicodeEscape(c);
            } else if (jsonEscapes[c] == null) {
                appendable.append(c);
            } else {
                appendable.append(jsonEscapes[c]);
            }
        }
        appendable.append('\"');
    }
    
    @Override
    public void outputUnicodeWithControls(String s) throws IOException {
        outputUnicodeNoControls(s);
    }
}
