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
package de.jpaw.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.DataOutput;


/**
 *          Functionality which corresponds to String, but for byte arrays.
 *          Essential feature is that the class is immutable, so you can use it in messaging without making deep copies.
 *          Mimicking {@link java.lang.String}, the class contains offset and length fields to allow sharing of the buffer.
 *          <p>
 *          This should really exist in Java SE already.
 *
 * @author Michael Bischoff
 *
 */


public final class ByteArray implements Externalizable, Cloneable {
    private static final long serialVersionUID = 2782729564297256974L;
    private static final int MAGIC_LENGTH_INDICATING_32_BIT_SIZE = 247;  // if a single byte length of this value is written in the
    // serialized form, it indicates a full four byte length must be read instead. Not used 0 or 255 due to their frequent use.

    private final byte [] buffer;
    private final int offset;
    private final int length;
    private ByteArray extraFieldJustRequiredForDeserialization = null;  // transient temporary field

    static private final byte[] ZERO_JAVA_BYTE_ARRAY = new byte [0];
    static public final ByteArray ZERO_BYTE_ARRAY = new ByteArray();

    public ByteArray() {
        // constructs an empty ByteArray. Does not really make sense.
        buffer = ZERO_JAVA_BYTE_ARRAY;
        offset = 0;
        length = 0;
    }

    // construct a ByteArray from a source byte []
    public ByteArray(byte [] source) {
        if (source == null || source.length == 0) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = source.clone();  // benchmarks have shown that clone() is equally fast as System.arraycopy for all lengths > 0
            offset = 0;
            length = buffer.length;
        }
    }

    // construct a ByteArray from a trusted source byte []
    private ByteArray(byte [] source, boolean unsafeTrustedReuseOfJavaByteArray) {
        if (source == null || source.length == 0) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = unsafeTrustedReuseOfJavaByteArray ? source : source.clone();
            offset = 0;
            length = buffer.length;
        }
    }

    // construct a ByteArray from a source byte [], with offset and length. source may not be null
    public ByteArray(byte [] source, int offset, int length) {
        if (source == null || offset < 0 || length < 0 || offset + length > source.length)
            throw new IllegalArgumentException();
        buffer = new byte[length];
        System.arraycopy(source, offset, buffer, 0, length);
        this.offset = 0;
        this.length = length;
    }

    // construct a ByteArray from another one
    // TODO: change it to private? external callers should use plain assignment or clone() instead!
    public ByteArray(ByteArray source) {
        if (source == null) {
            buffer = ZERO_JAVA_BYTE_ARRAY;
            offset = 0;
            length = 0;
        } else {
            buffer = source.buffer;  // no array copy required due to immutability
            offset = source.offset;
            length = source.length;
        }
    }

    // construct a ByteArray from a source byte [], with offset and length. source may not be null
    // TODO: change it to private? external callers should use the subArray() method
    public ByteArray(ByteArray source, int offset, int length) {
        if (source == null || offset < 0 || length < 0 || offset + length > source.length)
            throw new IllegalArgumentException();

        this.buffer = source.buffer;  // no array copy required due to immutability
        this.offset = source.offset + offset;
        this.length = length;
    }

    // same as above, but as a member method, ensuring
    public ByteArray subArray(int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > this.length)
            throw new IllegalArgumentException();
        // create a new ByteArray sharing the same buffer
        return new ByteArray(this, offset, length);
    }

    @Override
    public ByteArray clone() {
        return new ByteArray(this);
    }

    public int length() {
        return this.length;
    }

    public int indexOf(byte x) {
        int i = 0;
        while (i < length) {
            if (buffer[offset + i] == x)
                return i;
            ++i;
        }
        return -1;
    }

    public int indexOf(byte x, int fromIndex) {
        int i = fromIndex >= 0 ? fromIndex : 0;
        while (i < length) {
            if (buffer[offset + i] == x)
                return i;
            ++i;
        }
        return -1;
    }

    public int lastIndexOf(byte x) {
        int i = length;
        while (i > 0)
            if (buffer[offset + --i] == x)
                return i;
        return -1;
    }

    public int lastIndexOf(byte x, int fromIndex) {
        int i = fromIndex >= length ? length - 1: fromIndex;
        while (i >= 0) {
            if (buffer[offset + i] == x)
                return i;
            --i;
        }
        return -1;
    }

    public byte byteAt(int pos) {
        if (pos < 0 || pos >= length)
            throw new IllegalArgumentException();
        return buffer[offset + pos];
    }

    // return a defensive copy of the contents
    public byte [] getBytes() {
        byte [] result = new byte [length];
        System.arraycopy(buffer, offset, result, 0, length);
        return result;
    }

    // return a defensive copy of part of the contents. Shorthand for subArray(offset, length).getBytes(),
    // which would create a temporary object
    public byte [] getBytes(int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > this.length)
            throw new IllegalArgumentException();
        byte [] result = new byte [length];
        System.arraycopy(buffer, offset+this.offset, result, 0, length);
        return result;
    }

    private boolean contentEqualsSub(byte [] dst, int dstOffset, int dstLength) {
        if (length != dstLength)
            return false;
        for (int i = 0; i < dstLength; ++i)
            if (buffer[offset + i] != dst[dstOffset+i])
                return false;
        return true;
    }

    // following: all arguments must be not null
    public boolean contentEquals(ByteArray that) {
        return contentEqualsSub(that.buffer, that.offset, that.length);
    }
    public boolean contentEquals(byte [] that) {
        return contentEqualsSub(that, 0, that.length);
    }
    public boolean contentEquals(byte [] that, int thatOffset, int thatLength) {
        if (thatOffset < 0 || thatLength < 0 || thatOffset + thatLength > that.length)
            throw new IllegalArgumentException();
        return contentEqualsSub(that, thatOffset, thatLength);
    }

    // returns if the two instances share the same backing buffer (for debugging)
    public boolean shareBuffer(ByteArray that) {
        return buffer == that.buffer;
    }

    @Override
    public int hashCode() {
        int hash = 997;
        for (int i = 0; i < length; ++i)
            hash = 29 * hash + buffer[offset + i];
        return hash;
    }

    // two ByteArrays are considered equal if they have the same visible contents
    @Override
    public boolean equals(Object xthat) {
        if (xthat == null)
            return false;
        if (!(xthat instanceof ByteArray))
            return false;
        if (this == xthat)
            return true;
        ByteArray that = (ByteArray)xthat;
        // same as contentEqualsSub(..) now
        if (this.length != that.length)
            return false;
        for (int i = 0; i < length; ++i)
            if (buffer[offset + i] != that.buffer[that.offset + i])
                return false;
        return true;
    }
    
    // support function to allow dumping contents to DataOutput without the need to expose our internal buffer
    public void writeToDataOutput(DataOutput out) throws IOException {
        out.write(buffer, offset, length);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        //writeBytes(out, buffer, offset, length);
        if (length < 256 && length != MAGIC_LENGTH_INDICATING_32_BIT_SIZE) {
            out.writeByte(length);
        } else {
            out.writeByte(MAGIC_LENGTH_INDICATING_32_BIT_SIZE);
            out.writeInt(length);
        }
        out.write(buffer, offset, length);
    }

    // support function to allow ordinary byte [] to be written in same fashion
    static public void writeBytes(ObjectOutput out, byte [] buffer, int offset, int length) throws IOException {
        if (length < 256 && length != MAGIC_LENGTH_INDICATING_32_BIT_SIZE) {
            out.writeByte(length);
        } else {
            out.writeByte(MAGIC_LENGTH_INDICATING_32_BIT_SIZE);
            out.writeInt(length);
        }
        out.write(buffer, offset, length);
    }

    static public byte[] readBytes(ObjectInput in) throws IOException {
        int newlength = in.readByte();
        if (newlength < 0)
            newlength += 256;  // want full unsigned range
        if (newlength == MAGIC_LENGTH_INDICATING_32_BIT_SIZE) // magic to indicate four byte length
            newlength = in.readInt();

        // System.out.println("ByteArray.readExternal() with length " + newlength);
        if (newlength == 0)
            return ZERO_JAVA_BYTE_ARRAY;
        byte [] localBuffer = new byte[newlength];
        int done = 0;
        while (done < newlength) {
            int nRead = in.read(localBuffer, done, newlength-done);  // may return less bytes than requested!
            if (nRead <= 0)
                throw new IOException("deserialization of ByteArray returned " + nRead + " while expecting " + (newlength-done));
            done += nRead;
        }
        return localBuffer;
    }

    // factory method to read from objectInput via above helper function
    static public ByteArray read(ObjectInput in) throws IOException {
        return new ByteArray(readBytes(in), true);
    }

    // a direct implementation of this method would conflict with the immutability / "final" attributes of the field
    // Weird Java language design again. If readExternal() is kind of a constructor, why are assignments to final fields not allowed here?
    // alternatives around are to add artificial fields and use readResolve / proxies or to discard the "final" attributes,
    // or using reflection to set the values (!?). Bleh!
    // We're using kind of Bloch's "proxy" pattern (Essential Java, #78), namely a single-sided variant with just a single additonal member field,
    // which lets us preserve the immutability
    // see also http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6379948 for discussion around this
    @Override
    public void readExternal(ObjectInput in) throws IOException {
        extraFieldJustRequiredForDeserialization = new ByteArray(readBytes(in), true);
    }

    public Object readResolve() {
        // System.out.println("ByteArray.readResolve()");
        if (extraFieldJustRequiredForDeserialization == null)
            throw new RuntimeException("readResolve() called on instance not obtained via readExternal()");
        return extraFieldJustRequiredForDeserialization;
    }

    // factory method to construct a byte array from a prevalidated base64 byte sequence. returns null if length is suspicious
    static public ByteArray fromBase64(byte [] data, int offset, int length) {
        if (length == 0)
            return ZERO_BYTE_ARRAY;
        byte [] tmp = Base64.decode(data, offset, length);
        if (tmp == null)
            return null;
        return new ByteArray(tmp, true);
    }

    public void appendBase64(ByteBuilder b) {
        Base64.encodeToByte(b, buffer, offset, length);
    }
    public void appendToRaw(ByteBuilder b) {
        b.append(buffer, offset, length);
    }

    // returns the String representation of the visible bytes portion
    @Override
    public String toString() {
        return getBytes().toString();
    }
}
