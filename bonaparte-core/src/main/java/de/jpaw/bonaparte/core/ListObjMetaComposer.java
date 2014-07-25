package de.jpaw.bonaparte.core;

import java.util.List;

import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** Same as ListMetaComposer, but stores objects as a whole.
 * Useful when used as a delegate composer from folding. */
public class ListObjMetaComposer extends ListMetaComposer {
    
    /** Creates a new ListObjMetaComposer for a given preallocated external storage. */
    public ListObjMetaComposer(final List<DataAndMeta<Object,FieldDefinition>> storage, boolean doDeepCopies) {
        super(storage, doDeepCopies);
    }
    /** Creates a new ListObjMetaComposer, creating an own internal storage. */
    public ListObjMetaComposer(boolean doDeepCopies) {
        super(doDeepCopies);
    }

    @Override
    public void addField(ObjectReference di, BonaPortable obj) {
        storage.add(new DataAndMeta<Object,FieldDefinition>(di, obj));
    }
}
