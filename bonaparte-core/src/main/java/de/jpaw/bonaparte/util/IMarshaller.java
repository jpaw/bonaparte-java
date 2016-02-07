package de.jpaw.bonaparte.util;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.util.ByteArray;
import de.jpaw.util.ByteBuilder;

/** Defines the methods specific to a transmission format (for example XML, Bonaparte, JSON etc). */
public interface IMarshaller {
	
	/** Returns the content type implemented. Marshaller and unmarshaller must use the same content type. */
	String getContentType();
	
	/** Marshals the passed object into the Immutable ByteArray. */
	ByteArray marshal(BonaPortable request) throws Exception;
	
	/** Parses an object from the provided ByteBuilder. */
	BonaPortable unmarshal(ByteBuilder buffer) throws Exception;
}
