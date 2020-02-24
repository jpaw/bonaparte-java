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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.core.BonaCustom;
import de.jpaw.bonaparte.core.BonaPortable;
import de.jpaw.bonaparte.core.ByteArrayComposer;
import de.jpaw.bonaparte.core.ByteArrayConstants;
import de.jpaw.bonaparte.core.ByteArrayParser;
import de.jpaw.util.ByteArray;

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
    private boolean writeCRs = false;
    // the character set used for backend communication: UTF-8 or ISO-8859-something or windows-125x
    private Charset useCharset = ByteArray.CHARSET_UTF8; // Charset.defaultCharset(); or "windows-1252"
    private int initialBufferSize = 16000;  // start big to avoid frequent reallocation

    private void toStream(ByteArrayComposer w, OutputStream stream) throws IOException {
        stream.write(w.getBuffer(), 0, w.getLength());
        w.reset();
    }


    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
        ByteArrayComposer w = new ByteArrayComposer();
        w.setCharset(useCharset);

        if (Collection.class.isInstance(graph)) {
            w.startTransmission();
//            writeOptions(stream, w);
            toStream(w, stream);
            for (Object o : (Collection)graph) {
                w.writeRecord((BonaCustom) o);
                toStream(w, stream);
            }
            w.terminateTransmission();
            toStream(w, stream);
        } else {
            // assume single record
            w.writeRecord((BonaCustom) graph);
            toStream(w, stream);
        }
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
        // get the bytes, convert to String, parse
        // TODO: avoid byte buffer breaks within UTF-8-sequence!
        boolean isMultiRecord = false;
        byte [] byteBuffer = new byte[initialBufferSize];
        int numbytes = stream.read(byteBuffer, 0, initialBufferSize);
        logger.debug("read {} bytes from the input stream", numbytes);
        if (numbytes == initialBufferSize)
            throw new Exception("multi-reads for big messages not yet supported");
        if (byteBuffer[0] == ByteArrayConstants.TRANSMISSION_BEGIN)   // multi record (transmission)
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


    public boolean isWriteCRs() {
        return writeCRs;
    }

    public void setWriteCRs(boolean writeCRs) {
        this.writeCRs = writeCRs;
    }

    public Charset getUseCharset() {
        return useCharset;
    }

    public void setUseCharset(Charset useCharset) {
        this.useCharset = useCharset;
    }

    public int getInitialBufferSize() {
        return initialBufferSize;
    }

    public void setInitialBufferSize(int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    @Override
    public void start() {
        // nothing to do
    }

    @Override
    public void stop() {
        // nothing to do
    }
}
