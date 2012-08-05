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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.GregorianCalendar;

/**
 * The MessageComposer interface.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Defines the methods required for any serialization implementation
 */

public interface MessageComposer {
    // generic methods
    public void reset();	    // restart the output
    public int getLength();	    // obtain the number of written bytes (composer)
    public byte[] getBuffer();	// get the buffer (byte array of maybe too big size)
    public byte[] getBytes();	// get exact byte array of produced output
    
    // serialization methods: structure 
	public void writeNull();
    public void startTransmission();
    public void startRecord();
    public void startObject(String name, String version);
    public void startArray(int currentMembers, int maxMembers);
	public void writeSuperclassSeparator();  // this is bad. It should be transparent to the classes if the message format contains separators or not.
    public void terminateArray();
    public void terminateObject();
    public void terminateRecord();
    public void terminateTransmission();
	public void writeRecord(BonaPortable o);

    // serialization methods: field type specific
    void addEscapedString(String s,  int length);		// length is max length as specified in DSL
    void addField(String s,  int length);				// length is max length as specified in DSL
    void addField(Character c);
    void addField(Byte n);
    void addField(Short n);
    void addField(Double d);
    void addField(Float f);
    void addField(Long n);
    void addField(Integer n);
    void addField(Integer n, int length, boolean isSigned); // length is max length as specified in DSL
    void addField(BigDecimal n, int length, int decimals, boolean isSigned);
    void addField(Boolean b);
    void addField(byte [] b, int length);
    void addField(GregorianCalendar t, int length);
    void addField(BonaPortable obj);
    
    // methods from common settings
    public boolean doWriteCRs();
	public void setWriteCRs(boolean writeCRs);
	public Charset getCharset();
	public void setCharset(Charset charset);
}