package de.jpaw.util;

import java.io.IOException;

/** This interface allows to customize the way strings are output.
 * There is a default implementation which performs the Json standard encoding.
 * Applications can replace it with a custom implementation in order to allow for additional escaping requirements.    
 * 
 * Because a check for possible escapes takes more time, there are 3 methods, which distinguish by knowledge of the 
 * possible subset of characters which could occur.
 * 
 * All parameters must be non-null, or a NPE will occur. The check for null normally is done beforehand.
 * 
 * @author Michael Bischoff (jpaw@online.de)
 *
 */
public interface JsonEscaper {
    
    /** Writes a string of ASCII non-control characters. This is where we know we don't have to escape anything.
     * This method is used to output timestamp values, for example.
     * This normally never needs escaping.
     */
    public void outputAscii(String s) throws IOException;
    
    /** Writes a string of Unicode characters, but we know they cannot contain bytes less than ASCII 0x020, or quotes slashes or backslashes.
     * This method is used to output enum identifiers or Java class names or field names (i.e. valid Java identifiers).
     * This method does not need escaping in the Json default encoding, but may need some for specific implementations.  
     */
    public void outputUnicodeNoControls(String s) throws IOException ;
    
    /** Writes a string of possibly any Unicode character, including control characters. This will always need a check for escaping, but
     * the characters which need escaping can be different per implementation.
     * This method is used to output arbitrary strings.  
     */
    public void outputUnicodeWithControls(String s) throws IOException ;
}

