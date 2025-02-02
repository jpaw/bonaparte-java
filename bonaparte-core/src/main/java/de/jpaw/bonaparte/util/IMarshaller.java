package de.jpaw.bonaparte.util;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/** Defines the methods specific to a transmission format (for example XML, Bonaparte, JSON etc). */
public interface IMarshaller {

    /** Returns the content type implemented. Marshaller and unmarshaller must use the same content type. */
    String getContentType();

    /** Marshals the passed object into the Immutable ByteArray. */
    ByteArray marshal(BonaPortable request) throws Exception;

    /**
     * Parses an object from the provided ByteBuilder.
     * This method can return an Exception if only unmarshalling into an expected class is supported.
     */
    BonaPortable unmarshal(ByteBuilder buffer) throws Exception;

    /**
     * Parses an object from the provided ByteBuilder, specifying the class of the expected object.
     * This method can be overridden to support unmarshallers which need the expected result class (for example Jackson).
     */
    default <T extends BonaPortable> T unmarshal(ByteBuilder buffer, Class<T> resultClass) throws Exception {
        final BonaPortable result = unmarshal(buffer);
        if (result == null) {
            return null;
        }
        if (resultClass.isInstance(result)) {
            return resultClass.cast(result);
        }
        throw new MessageParserException(MessageParserException.WRONG_CLASS, result.getClass().getCanonicalName(), 0, resultClass.getCanonicalName());
    }
}
