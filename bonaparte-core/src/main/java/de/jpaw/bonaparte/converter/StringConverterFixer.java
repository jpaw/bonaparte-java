package de.jpaw.bonaparte.converter;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;

/** Sample implementation of StringConverter interface which performs several fixes on the data. */

public class StringConverterFixer extends StringConverterAbstract {

	private final boolean doTrimAll;
	private final boolean doTruncateAll;
	private final boolean doEmptyToNull;
	
	public StringConverterFixer(boolean doEmptyToNull, boolean doTruncateAll, boolean doTrimAll) {
		this.doTrimAll = doTrimAll;
		this.doTruncateAll = doTruncateAll;
		this.doEmptyToNull = doEmptyToNull;
	}
	
    @Override
    public String convert(String s, final AlphanumericElementaryDataItem meta) {
    	if (s == null)
    		return null;
    	
    	// step 1: trim if required or desired
    	if (doTrimAll || meta.getDoTrim())
    		s = s.trim();
    	
    	// step 2: truncate if required or desired and still too long
    	if ((doTruncateAll || meta.getDoTruncate()) && s.length() > meta.getLength()) {
    		s = s.substring(0, meta.getLength());
    		// this may result in a new string which does have trailing space again, so repeat the trim?
        	if (doTrimAll || meta.getDoTrim())
        		s = s.trim();
    	}
    	
    	// step 3: replace empty strings by null, if desired
    	if (doEmptyToNull && s.length() == 0)
    		return null;
    	else
    		return s;
    }

}
