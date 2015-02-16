package de.jpaw.bonaparte.refsw;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.pojos.apiw.Ref;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.util.ApplicationException;
import de.jpaw.util.ByteBuilder;

/** A composer of the compact format family, using classIds instead of names and replacing references to other classes by the key. */
public class ReferencingComposer extends CompactByteArrayComposer {
    private static final Ref DOES_NOT_MATCH_ANY = new Ref();
    private final Map<ClassDefinition,RefResolver<Ref, ?, ?>> resolvers;
    private Ref excludedObject = DOES_NOT_MATCH_ANY;       // an object not to replace, usually the outer one, in case the resolver map is created as a static object
    
    public ReferencingComposer(ByteBuilder out, Map<ClassDefinition,RefResolver<Ref, ?, ?>> resolvers) {
        super(out, true);
        this.resolvers = resolvers;
    }
    
    public void excludeObject(Ref obj) {
        excludedObject = obj == null ? DOES_NOT_MATCH_ANY : obj;
    }

    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        final RefResolver<Ref, ?, ?> r = di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
        if (r == null || obj == null || obj == excludedObject) {
            super.addField(di, obj);
        } else {
            // this is an object to replace by its reference
            try {
                addLong(r.getRef((Ref)obj));
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
