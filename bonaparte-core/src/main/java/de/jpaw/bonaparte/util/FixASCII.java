package de.jpaw.bonaparte.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.util.CharTestsASCII;

public class FixASCII {
    private static final Logger logger = LoggerFactory.getLogger(FixASCII.class);

    /**
     * <code>checkAsciiAndFixIfRequired()</code> tests if a String is indeed an ASCII string and does not exceed the maximum length. It will issue a warning and
     * replace offending characters with a question mark.
     *
     * @param s
     *            the string to test
     * @param maxlength
     *            if > 0, the maximum allowable string length
     * @return The sanitized string.
     */
    public static String checkAsciiAndFixIfRequired(String s, int maxlength) {
        if ((maxlength > 0) && (s.length() > maxlength)) {
            logger.warn("Application violating interface specs: trying to send contents which is too long: {} instead of {} characters", s.length(), maxlength);
            s = s.substring(0, maxlength);
        }
        if (!CharTestsASCII.isPrintableOrTab(s)) {
            // violated a test
            logger.warn("Application violating interface specs: illegal character in ASCII field");
            StringBuilder buff = new StringBuilder(s.length());
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (!CharTestsASCII.isAsciiPrintableOrTab(c)) {
                    buff.append('?');
                } else {
                    buff.append(c);
                }
            }
            s = buff.toString();
        }
        return s;
    }

}
