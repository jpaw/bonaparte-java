package de.jpaw.bonaparte.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.jpaw.bonaparte.pojos.meta.ObjectReference;

/** A composer which convertes the data into an immutable ByteArray.
 * Due to the possible self-similarity, child objects can participate in the parent's serialized form.
 * 
 * @author Michael Bischoff
 *
 */
public class ByteArrayComposerWithCache extends ByteArrayComposer {

    static public class OffsetInfo {
        // BonaPortable object;
        int start;
        int length;
        int minReferencedObject;
    }
    private OffsetInfo current = null;
    private Integer currentObjectIndex = -1;
    private List<OffsetInfo> mem = new ArrayList<OffsetInfo>(200);
    private Stack<Integer> backtrack = new Stack<Integer>();  // nesting depth. The current 
    
    public ByteArrayComposerWithCache() {
        super();
    }
    public ByteArrayComposerWithCache(ObjectReuseStrategy reuseStrategy) {
        super(reuseStrategy);
    }
    
    @Override
    public void writeRecord(BonaCustom o) {
        super.reset();
        mem.clear();
        backtrack.clear();
        current = null;
        currentObjectIndex = -1;
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
        backtrack.clear();
    }

    @Override
    public void startObject(ObjectReference di, BonaCustom obj) {
        backtrack.push(currentObjectIndex);
        currentObjectIndex = getNumberOfObjectsSerialized()-1;
        OffsetInfo current = new OffsetInfo();
        current.start = getLength();
        current.minReferencedObject = currentObjectIndex;
        mem.add(current);
        super.startObject(di, obj);
    }
    
    @Override
    public void terminateObject(ObjectReference di, BonaCustom obj) {
        super.terminateObject(di, obj);
        current.length = getLength() - current.start;
        // descend to the previous one. Compute referenced objects index
        currentObjectIndex = backtrack.pop();
        int tmp = current.minReferencedObject;
        current = mem.get(currentObjectIndex);
        if (tmp < current.minReferencedObject)
            current.minReferencedObject = tmp;
    }

    @Override
    protected void notifyReuse(int referencedIndex) {
        if (referencedIndex < current.minReferencedObject)
            current.minReferencedObject = referencedIndex;
    }
    
}
