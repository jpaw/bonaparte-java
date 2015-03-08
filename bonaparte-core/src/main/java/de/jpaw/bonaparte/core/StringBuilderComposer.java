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
package de.jpaw.bonaparte.core;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.bonaparte.pojos.meta.ObjectReference;
// according to http://stackoverflow.com/questions/469695/decode-base64-data-in-java , xml.bind is included in Java 6 SE
//import javax.xml.bind.DatatypeConverter;
/**
 * The StringBuilderComposer class.
 *
 * @author Michael Bischoff
 * @version $Revision$
 *
 *          Implements the serialization for the bonaparte format using StringBuilder.
 */

public class StringBuilderComposer extends AppendableComposer implements BufferedMessageComposer<IOException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringBuilderComposer.class);

    // variables set by constructor
    private final StringBuilder work;

    /** Quick conversion utility method, for use by code generators. (null safe) */
    public static String marshal(ObjectReference di, BonaPortable x) {
        if (x == null)
            return null;
        StringBuilder b = new StringBuilder(1000);
        new StringBuilderComposer(b).addField(di, x);
        return b.toString();
    }

    public String asString() {
        return work.toString();
    }

    public StringBuilderComposer(StringBuilder work) {
        super(work);
        this.work = work;
    }

    public StringBuilderComposer(StringBuilder work, ObjectReuseStrategy strategy) {
        super(work, strategy);
        this.work = work;
    }

    // restart the output
    @Override
    public void reset() {
        work.setLength(0);
        super.reset();   // reset cache!
    }

    @Override
    public final int getLength() {    // obtain the number of written bytes (composer)
        return work.length();
    }
    @Override
    public final byte[] getBuffer() {
        return getBytes();
    }

    @Override
    public final byte[] getBytes() {
        return work.toString().getBytes(getCharset());
    }

    /** Refine the main entry in order to relieve callers catching an Exception which is never thrown. */
    @Override
    public void writeRecord(BonaCustom obj) {
        try {
            super.writeRecord(obj);
        } catch (IOException e) {
            // StringBuilder.append does not throw an IOException.
            LOGGER.error("Got an IOException from within StringBuilder, which should not happen, really!", e);
            // to throw or not to throw (i.e. to ignore), that is the question...
            // Decision: By assumption, this cannot happen, so if it does, we should know about it!
            throw new RuntimeException("Got an IOException from within StringBuilder, which should not happen, really!", e);
        }
    }

    /** Refine the secondary entry in order to relieve callers catching an Exception which is never thrown. */
    @Override
    public void addField(ObjectReference di, BonaCustom obj) {
        try {
            super.addField(di, obj);
        } catch (IOException e) {
            // StringBuilder.append does not throw an IOException.
            LOGGER.error("Got an IOException from within StringBuilder, which should not happen, really!", e);
            // to throw or not to throw (i.e. to ignore), that is the question...
            // Decision: By assumption, this cannot happen, so if it does, we should know about it!
            throw new RuntimeException("Got an IOException from within StringBuilder, which should not happen, really!", e);
        }
    }
}
