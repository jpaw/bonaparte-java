 /*
  * Copyright 2012 Michael Bischoff
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package de.jpaw.bonaparte.camel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.extensions.ComposerExtensions;

/**
 * The NewDataFormat class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Implements the Apache Camel DataFormat interface using the ByteArray bonaparte implementation.
 *          Work in progress - needs major rework to provide better separation of outer transmission layer.
 */

public final class Bonaparte2ByteDataFormat implements DataFormat {
    private static final Logger logger = LoggerFactory.getLogger(Bonaparte2ByteDataFormat.class);
    private int initialBufferSize = 65500;  // start big to avoid frequent reallocation 
    
    private ByteArrayComposer w = null;
    
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        if (w == null)
            w = new ByteArrayComposer();  // create on demand
        if (java.util.List.class.isInstance(graph))
            ComposerExtensions.transmission(w, (List<BonaPortable>)graph);
        else
            w.writeRecord((BonaPortable) graph);
        stream.write(w.getBuffer(), 0, w.getLength());
    }

    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        // get the bytes, convert to String, parse
        // TODO: avoid byte buffer breaks within UTF-8-sequence!
        boolean isMultiRecord = false;
        byte [] byteBuffer = new byte[initialBufferSize];
        int numbytes = stream.read(byteBuffer, 0, initialBufferSize);
        logger.debug("read {} bytes from the input stream", numbytes);
        if (numbytes == initialBufferSize)
            throw new Exception("multi-reads for big messages not yet supported");
        if (byteBuffer[0] == '\024')   // multi record (transmission)
            isMultiRecord = true;
        
        ByteArrayParser p = new ByteArrayParser(byteBuffer, 0, numbytes);
        List<BonaPortable> resultSet = p.readTransmission();
        if (isMultiRecord)
            return resultSet;       // which may be empty
        else if (resultSet.size() == 0)
            return null;
        else {
            if (resultSet.size() > 1)
                throw new Exception("more than 1 record without a transmission header!");
            return resultSet.get(0);
        }
    }


    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    public void setInitialBufferSize(int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

}
