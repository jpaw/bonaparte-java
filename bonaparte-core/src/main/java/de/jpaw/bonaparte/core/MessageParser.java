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
import java.util.List;

/**
 * The MessageParser interface.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Defines the methods required for any parser implementation
 */

public interface MessageParser {
    // unmarshaller methods: field type specific
	public BigDecimal readBigDecimal(boolean allowNull, int length, int decimals, boolean isSigned)	throws MessageParserException;
	public Character  readCharacter (boolean allowNull) throws MessageParserException;
	public Boolean readBoolean  (boolean allowNull) throws MessageParserException;
	public Double  readDouble   (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Float   readFloat    (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Long    readLong     (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Integer readInteger  (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Short   readShort    (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Byte    readByte     (boolean allowNull, boolean isSigned) throws MessageParserException;
	public Integer readNumber   (boolean allowNull, int length, boolean isSigned)	throws MessageParserException;
	public String  readString   (boolean allowNull, int length, boolean doTrim, boolean allowCtrls, boolean allowUnicode) throws MessageParserException;
	public byte [] readRaw      (boolean allowNull, int length) throws MessageParserException;
	public GregorianCalendar readGregorianCalendar(boolean allowNull, int fractionalDigits) throws MessageParserException;
	
	// composite methods
	public BonaPortable readRecord() throws MessageParserException;
	public int parseArrayStart(int max, Class<? extends BonaPortable> type, int sizeOfChild) 	throws MessageParserException;
	public void parseArrayEnd()		throws MessageParserException;
	public BonaPortable readObject(Class<? extends BonaPortable> type, boolean allowNull, boolean allowSubtypes) throws MessageParserException; // parser factory
    public List<BonaPortable> readTransmission()   		throws MessageParserException;
    // helper functions
    public void eatParentSeparator() throws MessageParserException; // this is bad. It should be transparent to the classes if the message format contains separators or not.
    // upload of current class to be parsed: now all done locally within the parser
    // public String setCurrentClass(String classname);
    
    // methods from common settings
    public boolean doWriteCRs();
	public void setWriteCRs(boolean writeCRs);
	public Charset getCharset();
	public void setCharset(Charset charset);
}
