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

/**
 *          This class defines a couple of simple tests for the Java primitive type {@code byte} which correspond to the
 *          macros included in the header file ctype.h for the programming
 *          language C. These macros really should have been included in the
 *          standard Java class "Byte".
 *
 * @author Michael Bischoff
 *
 */

public class ByteTestsASCII {
    /**
     * The constructor is defined as private, in order to prevent that anyone
     * instantiates this class, which is not meaningful, because it contains
     * only static methods.
     */
    private ByteTestsASCII() {
    }

    /**
     * <code>isAsciiPrintable()</code> tests if a byte is a US-ASCII (7
     * bit) printable character, which mainly means that such a character is
     * available in every character encoding, no matter if single byte or
     * multi-byte.
     *
     * @param c
     *            the byte to test
     * @return <code>true</code> if the parameter represents a printable ASCII
     *         character, <code>false</code> otherwise.
     */
    public static boolean isAsciiPrintable(byte c) {
        return c >= 0x20 && c <= 0x7f;
    }

    /**
     * <code>isAsciiUpperCase()</code> tests if a byte is a US-ASCII (7
     * bit) printable character, and represents an English upper case letter
     * <code>(A .. Z)</code>.
     *
     * @param c
     *            the character to test
     * @return <code>true</code> if the parameter represents an upper case ASCII
     *         letter, <code>false</code> otherwise.
     */
    public static boolean isAsciiUpperCase(byte c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * <code>isAsciiLowerCase()</code> tests if a byte is a US-ASCII (7
     * bit) printable character, and represents an English lower case letter
     * <code>(a .. z)</code>.
     *
     * @param c
     *            the byte to test
     * @return <code>true</code> if the parameter represents an lower case ASCII
     *         letter, <code>false</code> otherwise.
     */
    public static boolean isAsciiLowerCase(byte c) {
        return c >= 'a' && c <= 'z';
    }

    /**
     * <code>isAsciiDigit()</code> tests if a byte is a US-ASCII (7 bit)
     * printable character, and represents a valid digit <code>(0 .. 9)</code>.
     *
     * @param c
     *            the byte to test
     * @return <code>true</code> if the parameter represents a digit,
     *         <code>false</code> otherwise.
     */
    public static boolean isAsciiDigit(byte c) {
        return c >= '0' && c <= '9';
    }

}
