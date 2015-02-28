package de.jpaw.bonaparte.refsp;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.api.AbstractRef;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.util.ApplicationException;

public class ReferencingParser extends CompactByteArrayParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencingParser.class);
    private final Map<ClassDefinition,RefResolver<AbstractRef, ?, ?>> resolvers;
    private boolean doSkipNext;        // skip the resolving for the next object (required if the outer object is in the map itself) 
    
    public ReferencingParser(byte[] buffer, int offset, int length, Map<ClassDefinition,RefResolver<AbstractRef, ?, ?>> resolvers, boolean skipFirst) {
        super(buffer, offset, length);
        this.resolvers = resolvers;
        this.doSkipNext = skipFirst;
    }
    
    public void skipNext() {
        doSkipNext = true;
    }

    protected RefResolver<AbstractRef, ?, ?> getReferencedResolver(ObjectReference di) {
        return di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
    }
    
    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws MessageParserException {
        if (doSkipNext) {
            doSkipNext = false;
            return super.readObject(di, type);
        }
        final RefResolver<AbstractRef, ?, ?> r = getReferencedResolver(di);
        if (r == null)
            return super.readObject(di, type);
        // read a long and resolve it
        if (checkForNull(di))
            return null;
        long ref = readLong(needToken(), di.getName());
        if (ref <= 0L)
            return null;        // mapping 0 => null
        
        try {
            BonaPortable newObject = r.getDTO(ref);
            if (newObject.getClass() != type) {
                // check if it is a superclass
                if (!di.getAllowSubclasses() || !type.isAssignableFrom(newObject.getClass())) {
                    throw newMPE(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                            newObject.getClass().getSimpleName(), type.getSimpleName(), di.getName(), Boolean.valueOf(di.getAllowSubclasses()))
                        );
                }
            }
            return type.cast(newObject);
        } catch (ApplicationException e) {
            throw newMPE(MessageParserException.INVALID_REFERENCES, e.getMessage());
        }
    }
    protected int collectionStart(FieldDefinition di, int storedCount) {
        if (storedCount != COLLECTION_COUNT_REF) {
            return storedCount; 
        } else {
            if (di instanceof ObjectReference) {
                final RefResolver<AbstractRef, ?, ?> r = getReferencedResolver((ObjectReference)di);
                if (r == null) {
                    LOGGER.warn("Resolver for {} not provided but referenced in stored instance of {}.{}",
                            ((ObjectReference)di).getLowerBound().getName(), currentClass, di.getName());
                }
            }
            return 0;  // currently always LAZY
        }
    }

    @Override
    public int parseMapStart(FieldDefinition di) throws MessageParserException {
        return collectionStart(di, super.parseMapStart(di));
    }

    @Override
    public int parseArrayStart(FieldDefinition di, int sizeOfElement) throws MessageParserException {
        return collectionStart(di, super.parseArrayStart(di, sizeOfElement));
    }
}
