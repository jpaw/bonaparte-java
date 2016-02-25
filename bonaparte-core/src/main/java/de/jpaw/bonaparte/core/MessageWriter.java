package de.jpaw.bonaparte.core;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Application visible portion of the MessageComposer.
 * Implementations of this interface are usually not thread safe, one instance should be created per outgoing channel.
 *
 * This API provides entry points to write a single object (BonaPortable), a record (wrapped BonaPortable) or a series of records (transmission).
 * In the first two cases, the lifecycle of the MessageWriter object consists of a single call, in the transmission case, three possibilities exist:
 * <p><ul>
 * <li>Invoking writeTransmission with a Collection parameter
 * <li>Invoking writeTransmission with an Iterable parameter
 * <li>Invoking steps manually: First startTransmission(), then any number of times writeRecord(), finally endTransmission().
 * <ul><p>
 * If the format requires separator characters between individual records, these will be inserted by the implementation.
 * Currently, only JSON is known to require such separators.
 * Some implementations (XML, JSON) will require information about the expected alternatives during initialization time.
 * Some implementations may need additional information, these should be passed to the implementation at construction time (XML top level element name).
 *
 * @author Michael Bischoff
 *
 * @param <E> - the Exception possibly thrown, IOException or RuntimeException (no exception).
 */

public interface MessageWriter<E extends Exception> extends Closeable {
    void writeTransmission(Collection<? extends BonaCustom> coll) throws E; // write a list of messages
    void writeTransmission(Iterable<? extends BonaCustom> coll) throws E;   // write a list of messages (different parameter type)

    void startTransmission() throws E;                                      // writes just header information, expecting a (possibly empty or single element) list of records
    void writeRecord(BonaCustom o) throws E;                                // writes a single record (prepended by a record separator, if required)
    void terminateTransmission() throws E;                                  // writes footer information

    void writeObject(BonaCustom o) throws E;                                // writes a single object, without record envelope (if called, it can only be the single invocation)

    // configuration methods

    /** Returns information about how an end-of-record is encoded.
     * @return true - if the current serializer writes a "carriage return / linefeed" end-of-record sequence (MS-WIN style), false if just a linefeed (UNIX / LINUX style). */
    public boolean getWriteCRs();

    /** Changes the end-of-record character sequence for the current serializer. Not relevant for deserializers.
     *  The initial behavior is set via a static class variable, which can be set via setDefaultWriteCRs(boolean).
     *
     * @param writeCRs - true means write a "carriage return / linefeed" sequence, false means write just a linefeed. */
    public void setWriteCRs(boolean writeCRs);

    /** Retrieves the current setting, in which encoding conversions between Strings and byte arrays are done for Unicode characters.
     *
     * @return the current {@link java.nio.charset.Charset} setting.
     */
    public Charset getCharset();

    /** Specifies the encoding for conversions between Strings and byte arrays.
     *  The initial value is determined by a static class variable, which can be set via {@setDefaultCharset}.
     *
     * @param charset - the encoding for future conversions.
     *  If the encoding is changed within a conversion process, i.e. after data has been serialized, but before it has been retrieved, the result is implementation dependent.
     */
    public void setCharset(Charset charset);
}
