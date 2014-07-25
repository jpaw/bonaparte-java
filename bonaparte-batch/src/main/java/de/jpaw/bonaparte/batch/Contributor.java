package de.jpaw.bonaparte.batch;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;

/** Defines the method any module must provide, to provide and process command line parameters.
 * Implementations will build on top of this and provide task specific additions.
 *
 */
public interface Contributor {
    public void addCommandlineParameters(JSAP params) throws Exception;
    public void evalCommandlineParameters(JSAPResult params) throws Exception;
    public void close() throws Exception;   // ends any processing (can be used to close files or connections)
}
