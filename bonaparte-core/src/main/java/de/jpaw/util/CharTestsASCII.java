/*
 * Copyright 2012 Michael Bischoff
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jpaw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *          This class defines a couple of simple tests for the Java primitive type {@code char} which correspond to the
 *          macros included in the header file ctype.h for the programming
 *          language C. These macros really should have been included in the
 *          standard Java class "Character", but that class seems to focus more
 *          on localization dependent tests rather than file processing.
 *          There is an implementation in apache.commons.lang.CharUtils,
 *          but for such a small functionality we avoid an external dependency and
 *          create the required methods here.
 *
 * @author Michael Bischoff
 *
 */

public class CharTestsASCII {
    private static final Logger logger = LoggerFactory.getLogger(CharTestsASCII.class);

    public static final Pattern UPPERCASE_PATTERN = Pattern.compile("\\A[A-Z]*\\z");  // tests if a field consists of uppercase characters only
    public static final Pattern LOWERCASE_PATTERN = Pattern.compile("\\A[a-z]*\\z");  // tests if a field consists of lowercase characters only

    /**
     * The constructor is defined as private, in order to prevent that anyone
     * instantiates this class, which is not meaningful, because it contains
     * only static methods.
     */
    private CharTestsASCII() {
    }

    public static boolean isUpperCase(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiUpperCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isLowerCase(String s) {
        for (int i = 0; i < s.length(); ++i) {
            if (!isAsciiLowerCase(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    // same functionality, manual implementation. Which one is faster?
    public static boolean isUpperCaseByPattern(String s) {
        Matcher m = UPPERCASE_PATTERN.matcher(s);
        return m.find();
    }
    public static boolean isLowerCaseByPattern(String s) {
        Matcher m = LOWERCASE_PATTERN.matcher(s);
        return m.find();
    }

    /**
     * <code>isAsciiPrintable()</code> tests if a character is a US-ASCII (7
     * bit) printable character, which mainly means that such a character is
     * available in every character encoding, no matter if single byte or
     * multi-byte.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents a printable ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiPrintable(char c) {
        return (c >= 0x20) && (c <= 0x7f);
    }

    /**
     * <code>isAsciiUpperCase()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English upper case character
     * <code>(A .. Z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiUpperCase(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    /**
     * <code>isAsciiLowerCase()</code> tests if a character is a US-ASCII (7
     * bit) printable character, and represents an English lower case character
     * <code>(a .. z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an lower case ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiLowerCase(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    /**
     * <code>isAsciiDigit()</code> tests if a character is a US-ASCII (7 bit)
     * printable character, and represents a valid digit <code>(0 .. 9)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents a digit,
     *         <code>false</code> otherwise.
     */
    public static boolean isAsciiDigit(char c) {
        return (c >= '0') && (c <= '9');
    }

    /**
     * <code>checkAsciiAndFixIfRequired()</code> tests if a String is indeed an ASCII string and does not exceed tge maximum length. It will issue a warning and
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
        // check contents
        int i;
        for (i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if ((c != '\t') && !CharTestsASCII.isAsciiPrintable(c)) {
                break;
            }
        }
        if (i != s.length()) {
            // violated a test
            logger.warn("Application violating interface specs: illegal character in ASCII field: code {}", Character.codePointAt(s, i));
            StringBuilder buff = new StringBuilder(s.length());
            for (i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if ((c != '\t') && !CharTestsASCII.isAsciiPrintable(c)) {
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
