package de.jpaw.bonaparte.refsp;

import java.util.Map;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.CompactByteArrayComposer;
import de.jpaw.bonaparte.pojos.apip.Ref;
import de.jpaw.bonaparte.pojos.meta.ClassDefinition;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
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

    protected RefResolver<Ref, ?, ?> getReferencedResolver(ObjectReference di) {
        return di.getLowerBound() == null ? null : resolvers.get(di.getLowerBound());
    }
    
    @Override
    public void startMap(FieldDefinition di, int currentMembers) {
        out.writeByte(MAP_BEGIN);
        // write the map count or an indicator if the object is external (because size could change independently then)
        // important: the provided map must be identical for composing and parsing
        if (di instanceof ObjectReference && getReferencedResolver((ObjectReference)di) != null) {
            intOut(COLLECTION_COUNT_REF);
        } else {
            intOut(currentMembers);
        }
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) {
        out.writeByte(ARRAY_BEGIN);
        // write the array count or an indicator if the object is external (because size could change independently then)
        // important: the provided map must be identical for composing and parsing
        if (di instanceof ObjectReference && getReferencedResolver((ObjectReference)di) != null) {
            intOut(COLLECTION_COUNT_REF);
        } else {
            intOut(currentMembers);
        }
    }
    
    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        final RefResolver<Ref, ?, ?> r = getReferencedResolver(di);
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
