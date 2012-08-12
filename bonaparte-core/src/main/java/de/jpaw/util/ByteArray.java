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


/**
 * The ByteArray class.
 * 
 * @author Michael Bischoff
 * @version $Revision$
 * 
 *          Functionality which corresponds to String, but for byte arrays.
 *          Essential feature is that the class is immutable, so you can use it in messaging without making deep copies.
 *          Mimicking java.lang.String, the class contains offset and length fields to allow sharing of the buffer. 
 *          This should really exist in Java SE already.
 */


public final class ByteArray {
	private final byte [] buffer;	
	private final int offset;
	private final int length;
	
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

	// construct a ByteArray from a source byte []
	public ByteArray(byte [] source, boolean unsafeTrustedReuseOfJavaByteArray) {
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
		System.arraycopy(source, 0, buffer, 0, length);
		this.offset = 0;
		this.length = length;
	}

	// construct a ByteArray from another one
	// TODO: change it to private? external callers should use plain assignment instead!
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
}
