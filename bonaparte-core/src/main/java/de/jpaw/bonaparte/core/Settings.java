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
package de.jpaw.bonaparte.core;

import java.nio.charset.Charset;

/**
 * Defines the parameters for most serializers / marshallers and deserializers / unmarshallers of the bonaparte format.
 * 
 * @author Michael Bischoff
 * 
 */
public abstract class Settings {
    // Java 7 required:
    // static private boolean defaultCRs = System.lineSeparator().length() == 2; // on Unix: false, on Windows: true
    static private boolean defaultCRs = System.getProperty("line.separator").length() == 2; // on Unix: false, on Windows: true
    static private Charset defaultCharset = Charset.forName("UTF-8");            // always use UTF-8 unless explicitly requested differently

    private boolean writeCRs;   // determines the record terminator sequence. Attempts to mimic text file line breaks of the OS
    private Charset charset; // usually UTF-8, can be explicitly set to some other encoding, if desired (usually some single-byte fixed width character set)

    /** Returns information about how an end-of-record is encoded. Only relevant for serializers, not for deserializers.
     * @return true - if the current serializer writes a "carriage return / linefeed" end-of-record sequence (MS-WIN style), false if just a linefeed (UNIX / LINUX style). */
    public boolean doWriteCRs() {
        return writeCRs;
    }

    /** Changes the end-of-record character sequence for the current serializer. Not relevant for deserializers.
     *  The initial behaviour is set via a static class variable, which can be set via {@link setDefaultWriteCRs}.
     * 
     * @param writeCRs - true means write a "carriage return / linefeed" sequence, false means write just a linefeed. */
    public void setWriteCRs(boolean writeCRs) {
        this.writeCRs = writeCRs;
    }

    /** Retrieves the current setting, in which encoding conversions between Strings and byte arrays are done for Unicode characters.
     * 
     * @return the current {@link java.nio.Charset} setting.
     */
    public Charset getCharset() {
        return charset;
    }

    /** Specifies the encoding for conversions between Strings and byte arrays.
     *  The initial value is determined by a static class variable, which can be set via {@setDefaultCharset}.
     * 
     * @param charset - the encoding for future conversions.
     *  If the encoding is changed within a conversion process, i.e. after data has been serialized, but before it has been retrieved, the result is implementation dependent.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /** Creates a new settings instance. Will only be called from superclasses. */
    protected Settings() {
        writeCRs = defaultCRs;
        charset = defaultCharset;
    }


    /** Returns information about how an end-of-record will be encoded for new instances of this class constructed in the future.
     * @return the current default setting. */
    public static boolean getDefaultWriteCRs() {
        return defaultCRs;
    }

    /** Changes the end-of-record character sequence for instances of this class constructed in the future.
     *  The initial behaviour is operating system dependent, it is "CR/LF" for MS-Windows and "LF" for Unix/Linux.
     * 
     * @param writeCRs - true means write a "carriage return / linefeed" sequence, false means write just a linefeed. */
    public static void setDefaultWriteCRs(boolean writeCRs) {
        defaultCRs = writeCRs;
    }

    /** Returns information about which character encoding will be used for new instances of this class constructed in the future.
     * @return the current default {@link java.nio.Charset} setting. */
    public static Charset getDefaultCharset() {
        return defaultCharset;
    }

    /** Specifies the initial encoding for conversions between Strings and byte arrays for instances of this class constructed in the future.
     *  The load-time default is set to "UTF-8".
     * 
     * @param charset - the encoding for future classes.
     */
    public static void setDefaultCharset(Charset charset) {
        defaultCharset = charset;
    }
}
