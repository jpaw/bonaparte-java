package de.jpaw.bonaparte.core;

import java.util.List;

import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** Same as ListComposer, but stores objects as a whole.
 * Useful when used as a delegate composer from folding. */
public class ListObjComposer extends ListComposer {

    /** Creates a new ListComposer for a given preallocated external storage. */
    public ListObjComposer(final List<Object> storage, boolean doDeepCopies) {
    	super(storage, doDeepCopies);
    }
    
    /** Creates a new ListComposer, creating an own internal storage. */
    public ListObjComposer(boolean doDeepCopies) {
    	super(doDeepCopies);
    }
    
    @Override
    public void addField(ObjectReference di, BonaPortable obj) {
    	storage.add(obj);
    }
}
