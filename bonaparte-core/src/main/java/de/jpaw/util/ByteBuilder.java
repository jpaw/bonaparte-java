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

    // extend the buffer because we ran out of space
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
    // StringBuilder compatibility function
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > currentAllocSize)
            createMoreSpace(minimumCapacity - currentLength);
    }
    // set the length of the contents, assuming contents has been added externally
    // external class must have obtained buffer through getCurrentBuffer() after calling ensureCapacity and getLength()
    public void setLength(int newLength) {
        if (newLength > currentAllocSize)
            throw new IndexOutOfBoundsException();
        currentLength = newLength;
    }
    public void append(byte b) {
        if (currentLength >= currentAllocSize)
            createMoreSpace(1);
        buffer[currentLength++] = b;
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

    public String toString() {
        return new String(buffer, 0, currentLength, charset);
    }
}
