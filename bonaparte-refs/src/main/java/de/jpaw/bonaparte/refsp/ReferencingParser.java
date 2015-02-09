package de.jpaw.bonaparte.refsp;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.apip.Ref;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.refs.PersistenceException;

public class ReferencingParser extends CompactByteArrayParser {
    private final Map<ClassDefinition,RefResolver<Ref, ?, ?>> resolvers;
    private boolean doSkipNext;        // skip the resolving for the next object (required if the outer object is in the map itself) 
    
    public ReferencingParser(byte[] buffer, int offset, int length, Map<ClassDefinition,RefResolver<Ref, ?, ?>> resolvers, boolean skipFirst) {
        super(buffer, offset, length);
        this.resolvers = resolvers;
        this.doSkipNext = skipFirst;
    }
    
    public void skipNext() {
        doSkipNext = true;
    }

    @Override
    public <R extends BonaPortable> R readObject (ObjectReference di, Class<R> type) throws MessageParserException {
        if (doSkipNext) {
            doSkipNext = false;
            return super.readObject(di, type);
        }
        final RefResolver<Ref, ?, ?> r = di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
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
        } catch (PersistenceException e) {
            throw newMPE(MessageParserException.INVALID_REFERENCES, e.getMessage());
        }
    }
}
