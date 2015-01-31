package de.jpaw.bonaparte.refs;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.refs.RefLong;
import de.jpaw.util.ByteBuilder;

/** A composer of the compact format family, using classIds instead of names and replacing references to other classes by the key. */
public class ReferencingComposer extends CompactByteArrayComposer {
    private static final RefLong DOES_NOT_MATCH_ANY = new RefLong();
    private final Map<ClassDefinition,RefResolver<RefLong,?>> resolvers;
    private RefLong excludedObject = DOES_NOT_MATCH_ANY;       // an object not to replace, usually the outer one, in case the resolver map is created as a static object
    
    public ReferencingComposer(ByteBuilder out, Map<ClassDefinition,RefResolver<RefLong,?>> resolvers) {
        super(out, true);
        this.resolvers = resolvers;
    }
    
    public void excludeObject(RefLong obj) {
        excludedObject = obj == null ? DOES_NOT_MATCH_ANY : obj;
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        final RefResolver<RefLong,?> r = di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
        if (r == null || obj == excludedObject) {
            super.addField(di, obj);
        } else {
            // this is an object to replace by its reference
            addLong(r.getRef((RefLong)obj));
        }
    }
}
