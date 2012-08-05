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

import java.nio.charset.Charset;

/**
 * The Settings class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Defines the parameters for most serializers / marshallers and deserializers / unmarshallers
 */
public abstract class Settings {
	static private final boolean defaultCRs = System.lineSeparator().length() == 2;  // on Unix: false, on Windows: true
	static private final Charset defaultCharset = Charset.forName("UTF-8");			 // always use UTF-8 unless explicitly requested differently
	
	private boolean writeCRs;   // determines the record terminator sequence. Attempts to mimic text file line breaks of the OS
	private Charset charset; // usually UTF-8, can be explicitly set to some other encoding, if desired (usually some single-byte fixed width character set)
	
	
    public boolean doWriteCRs() {
		return writeCRs;
	}

	public void setWriteCRs(boolean writeCRs) {
		this.writeCRs = writeCRs;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}


    protected Settings() {
    	writeCRs = defaultCRs;
    	charset = defaultCharset;
    }
    
}
