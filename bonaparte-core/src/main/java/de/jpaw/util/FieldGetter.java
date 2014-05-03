package de.jpaw.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.FoldingComposer;
import de.jpaw.bonaparte.core.ListComposer;
import de.jpaw.bonaparte.pojos.meta.FoldingStrategy;

/** Utility method to get a fields from BonaPortables via pathname. */
public class FieldGetter {
	public static List<Object> getFields(BonaPortable obj, List<String> fieldnames) {
		if (obj == null || fieldnames == null)
			return null;
		
		// step 1: construct the output buffer
		List<Object> target = new ArrayList<Object>(fieldnames.size());		// the list to write the field into
		
		// step 2: create and chain the message composers
		ListComposer writer = new ListComposer(target, false);
		Map<Class<? extends BonaPortable>, List<String>> mapping = Collections.<Class<? extends BonaPortable>, List<String>>singletonMap(BonaPortable.class, fieldnames); 
		new FoldingComposer<RuntimeException>(writer, mapping, FoldingStrategy.FORWARD_OBJECTS).writeRecord(obj);

		return target;
	}
	
	public static Object getField(BonaPortable obj, String fieldname) {
		List<Object> target = getFields(obj, Collections.singletonList(fieldname));		// the list with the result
		return (target.size() == 0) ? null : target.get(0);
	}

}
