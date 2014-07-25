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

import java.nio.charset.Charset;

/**
 *          Functionality which corresponds to StringBuilder, but for byte arrays.
 *          <p>
 *          This should really exist in Java SE already.
 *
 * @author Michael Bischoff
 *
 */

public class ByteBuilder {
    // static variables
    private static final int DEFAULT_INITIAL_CAPACITY = 65502; // 64 KB - 32 Byte for overhead
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");    // default character set is available on all platforms

    // per instance variables
    private Charset charset;
    private int currentAllocSize;
    private int currentLength;
    private byte[] buffer;

    // set all private variables except charset
    private final void constructorHelper(int size) {
        buffer = new byte[size];
        currentAllocSize = size;
        currentLength = 0;
    }

    public ByteBuilder() {  // default constructor
        constructorHelper(DEFAULT_INITIAL_CAPACITY);
        charset = DEFAULT_CHARSET;
    }
    public ByteBuilder(int initialSize, Charset charset) {  // constructor with possibility to override settings
        constructorHelper(initialSize > 0 ? initialSize : DEFAULT_INITIAL_CAPACITY);
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }

    /** Extend the buffer because we ran out of space. */
    private void createMoreSpace(int minimumRequired) {
        // allocate the space
        int newAllocSize = 2 * currentAllocSize;
        if (newAllocSize < currentLength + minimumRequired)
            newAllocSize = currentLength + minimumRequired;
        byte [] newBuffer = new byte[newAllocSize];
        // uuuh, at this point we allocate the old plus the new space
        // see if we have to transfer data
        if (currentLength > 0)
            System.arraycopy(buffer, 0, newBuffer, 0, currentLength);
        // assign the new variables
        buffer = newBuffer;
        currentAllocSize = newAllocSize;
    }
    /** StringBuilder compatibility function: ensure that the total space is at least as requested. */
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > currentAllocSize)
            createMoreSpace(minimumCapacity - currentLength);
    }
    /** Ensure that at least delta bytes are left in the buffer, extending the buffer if required. */
    public void require(int delta) {
        if (currentLength + delta > currentAllocSize)
            createMoreSpace(currentLength + delta - currentLength);
    }
    
    /** Sets the length of the contents, assuming contents has been added externally.
     * External class must have obtained buffer through getCurrentBuffer() after calling ensureCapacity and getLength(). 
     * @param newLength
     */
    public void setLength(int newLength) {
        if (newLength > currentAllocSize)
            throw new IndexOutOfBoundsException();
        currentLength = newLength;
    }
    /** Advances the current position by some positive integer, assuming contents has been added externally.
     * External class must have obtained buffer through getCurrentBuffer() after calling ensureCapacity and getLength(). 
     * @param delta
     */
    public void advanceBy(int delta) {
        if (delta < 0 || currentLength + delta > currentAllocSize)
            throw new IndexOutOfBoundsException();
        currentLength += delta;
    }
    /** Compatibility method. */
    public void writeByte(int data) {
        append((byte)data);
    }
    /** Append a byte to the buffer. */
    public void append(byte b) {
        if (currentLength >= currentAllocSize)
            createMoreSpace(1);
        buffer[currentLength++] = b;
    }
    /** Append a short to the buffer. High endian. */
    public void append(short n) {
        if (currentLength + 2 > currentAllocSize)
            createMoreSpace(2);
        buffer[currentLength++] = (byte) (n >>> 8);
        buffer[currentLength++] = (byte) n;
    }
    /** Append an int to the buffer. High endian. */
    public void append(int n) {
        if (currentLength + 4 > currentAllocSize)
            createMoreSpace(4);
        buffer[currentLength] = (byte) (n >>> 24);
        buffer[currentLength+1] = (byte) (n >>> 16);
        buffer[currentLength+2] = (byte) (n >>> 8);
        buffer[currentLength+3] = (byte) n;
        currentLength += 4;
    }
    /** Append a long to the buffer. High endian. */
    public void append(long n) {
        if (currentLength + 8 > currentAllocSize)
            createMoreSpace(8);
        int nn = (int)(n >> 32);
        buffer[currentLength] = (byte) (nn >>> 24);
        buffer[currentLength+1] = (byte) (nn >>> 16);
        buffer[currentLength+2] = (byte) (nn >>> 8);
        buffer[currentLength+3] = (byte) nn;
        buffer[currentLength+4] = (byte) (n >>> 24);
        buffer[currentLength+5] = (byte) (n >>> 16);
        buffer[currentLength+6] = (byte) (n >>> 8);
        buffer[currentLength+7] = (byte) n;
        currentLength += 8;
    }
    // append another byte array
    public void append(byte [] array) {
        int length = array.length;
        if (length > 0) {
            if (currentLength + length > currentAllocSize)
                createMoreSpace(length);
            System.arraycopy(array, 0, buffer, currentLength, length);
            currentLength += length;
        }
    }
    // append part of another byte array
    public void append(byte [] array, int offset, int length) {
        if (length > 0) {
            if (currentLength + length > currentAllocSize)
                createMoreSpace(length);
            System.arraycopy(array, offset, buffer, currentLength, length);
            currentLength += length;
        }
    }
    public void append(String s) {
        if (s.length() > 0) {
            append(s.getBytes(charset));
        }
    }
    // append a single double-byte character (BMP 0)
    public void appendUnicode(int c) {
        if (c <= 127) {
            // ASCII character: this is faster
            append((byte)c);
        } else {
            // this is weird! Can't we do it better?
            int [] tmp = new int [1];
            tmp[0] = c;
            append(new String(tmp, 0, 1).getBytes(charset));
        }
    }
    // append the contents of String, assuming all characters are single-byte. No test is done. Argument must not be null.
    public void appendAscii(String s) {
        int length = s.length();
        if (currentLength + length > currentAllocSize)
            createMoreSpace(length);
        for (int i = 0; i < length; ++i)
            buffer[currentLength++] = (byte) s.charAt(i);
    }
    public void appendAscii(StringBuilder s) {
        int length = s.length();
        if (currentLength + length > currentAllocSize)
            createMoreSpace(length);
        for (int i = 0; i < length; ++i)
            buffer[currentLength++] = (byte)s.charAt(i);
    }

    public byte byteAt(int pos) {
        if (pos < 0 || pos >= currentLength)
            throw new IndexOutOfBoundsException();
        return buffer[pos];
    }

    // getBytes() can be very slow!
    // beware, this call needs to be accompanied by length()!
    public byte[] getCurrentBuffer() {
        return buffer;
    }
    public int length() {
        return currentLength;
    }

    // returns a defensive copy of the contents
    public byte[] getBytes() {
        byte [] tmp = new byte[currentLength];
        System.arraycopy(buffer, 0, tmp, 0, currentLength);
        return tmp;
    }

    @Override
    public String toString() {
        return new String(buffer, 0, currentLength, charset);
    }
}
