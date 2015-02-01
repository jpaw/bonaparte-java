package de.jpaw.bonaparte.refs;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.CompactByteArrayParser;
import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.api.Ref;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

public class ReferencingParser extends CompactByteArrayParser {
    private final Map<ClassDefinition,RefResolver<Ref,?>> resolvers;
    private boolean doSkipNext;        // skip the resolving for the next object (required if the outer object is in the map itself) 
    
    public ReferencingParser(byte[] buffer, int offset, int length, Map<ClassDefinition,RefResolver<Ref,?>> resolvers, boolean skipFirst) {
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
        final RefResolver<Ref,?> r = di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
        if (r == null)
            return super.readObject(di, type);
        // read a long and resolve it
        long ref = readLong(needToken(), di.getName());
        if (ref <= 0L)
            return null;
        BonaPortable newObject = r.getDTO(ref);
        if (newObject.getClass() != type) {
            // check if it is a superclass
            if (!di.getAllowSubclasses() || !type.isAssignableFrom(newObject.getClass())) {
                throw newMPE(MessageParserException.BAD_CLASS, String.format("(got %s, expected %s for %s, subclassing = %b)",
                        newObject.getClass().getSimpleName(), type.getSimpleName(), di.getName(), di.getAllowSubclasses())
                    );
            }
        }
        return type.cast(newObject);
    }

}
