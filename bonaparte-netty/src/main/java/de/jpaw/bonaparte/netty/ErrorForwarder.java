package de.jpaw.bonaparte.netty;

import de.jpaw.bonaparte.core.BonaPortable;

/**
 * Interface can be implemented to convert unparseable messages into well-defined error messages which are handed upstream, in order to allow appropriate
 * logging and creation of a correct response message.
 * 
 */
public interface ErrorForwarder {
    public BonaPortable createErrorObject(int errorCode, String errorDetails, byte[] rawData);
}
