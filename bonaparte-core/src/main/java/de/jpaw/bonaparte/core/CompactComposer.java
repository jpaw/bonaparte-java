package de.jpaw.bonaparte.core;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import de.jpaw.bonaparte.pojos.meta.AlphanumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BasicNumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.BinaryElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.EnumDataItem;
import de.jpaw.bonaparte.pojos.meta.FieldDefinition;
import de.jpaw.bonaparte.pojos.meta.MiscElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.NumericElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.ObjectReference;
import de.jpaw.bonaparte.pojos.meta.TemporalElementaryDataItem;
import de.jpaw.bonaparte.pojos.meta.XEnumDataItem;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.enums.XEnum;
import de.jpaw.util.ByteArray;

/** 
 * Composer intended for IMDGs.
 * Goals: 
 * 1) be as efficient as reasonable, to save memory (= allows to cache more objects in same amount of RAM = more speed)
 * 2) don't allocate temporary objects during serialization (avoid GC overhead), unless absolutely required
 *  
 * @author Michael Bischoff
 *
 */
public class CompactComposer extends CompactConstants implements MessageComposer<IOException> {
	private static Field unsafeString = calculateUnsafe();
	static private Field calculateUnsafe() {
		try {
			Field f = String.class.getDeclaredField("value");
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
			return null;
		}
	}

    private final boolean useCache;
    private final Map<BonaPortable, Integer> objectCache;
    private int numberOfObjectsSerialized;
    private int numberOfObjectReuses;
    // variables set by constructor
    protected final DataOutput out;
    protected final boolean recommendIdentifiable;

    // entry called from generated objects: (Object header has been written already by internal methods (and unfortunately in some different fashion...))
    public static void serialize(BonaPortable obj, DataOutput _out, boolean recommendIdentifiable) throws IOException {
    	MessageComposer<IOException> _w = new CompactComposer(_out, recommendIdentifiable);
    	obj.serializeSub(_w);
    	_w.terminateObject(StaticMeta.OUTER_BONAPORTABLE, obj);
    }

    public CompactComposer(DataOutput out, boolean recommendIdentifiable) {
        this(out, ObjectReuseStrategy.defaultStrategy, recommendIdentifiable);
    }

    /**
     * Creates a new ByteArrayComposer, using this classes static default
     * Charset
     **/
    public CompactComposer(DataOutput out, ObjectReuseStrategy reuseStrategy, boolean recommendIdentifiable) {
        switch (reuseStrategy) {
        case BY_CONTENTS:
            this.objectCache = new HashMap<BonaPortable, Integer>(250);
            this.useCache = true;
            break;
        case BY_REFERENCE:
            this.objectCache = new IdentityHashMap<BonaPortable, Integer>(250);
            this.useCache = true;
            break;
        default:
            this.objectCache = null;
            this.useCache = false;
            break;
        }
        this.out = out;
        this.recommendIdentifiable = recommendIdentifiable;
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
    }

    // must be overridden / called if caching / reuse is active!
    public void reset() {
        numberOfObjectsSerialized = 0;
        numberOfObjectReuses = 0;
        if (useCache)
            objectCache.clear();

    }

    // for statistics
    public int getNumberOfObjectReuses() {
        return numberOfObjectReuses;
    }

    /**************************************************************************************************
     * Serialization goes here
     **************************************************************************************************/

    /**
     * allows to add raw data to the produced byte array. Use this for protocol
     * support at beginning or end of a message
     * 
     * @throws IOException
     */

    protected void writeNull() throws IOException {
        out.writeByte(NULL_FIELD);
    }

    protected void writeEmpty() throws IOException {
        out.writeByte(EMPTY_FIELD);
    }

    @Override
    public void writeNull(FieldDefinition di) throws IOException {
        out.writeByte(NULL_FIELD);
    }

    @Override
    public void writeNullCollection(FieldDefinition di) throws IOException {
        out.writeByte(NULL_FIELD);
    }

    @Override
    public void startTransmission() throws IOException {
    }

    @Override
    public void terminateTransmission() throws IOException {
    }

    @Override
    public void terminateRecord() throws IOException {
    }

    @Override
    public void writeSuperclassSeparator() throws IOException {
        out.writeByte(PARENT_SEPARATOR);
    }

    @Override
    public void startRecord() throws IOException {
    }

    @Override
    public void writeRecord(BonaPortable o) throws IOException {
        startRecord();
        addField(StaticMeta.OUTER_BONAPORTABLE, o);
        terminateRecord();
    }

    // write a non-empty string (using charAt())
    protected void writeLongString(String s) throws IOException {
    	int maxCode = 0;
    	int len = s.length();
    	for (int i = 0; i < len; ++ i) {
    		int c = (int)s.charAt(i);
    		if (c > maxCode)
    			maxCode = c;
    	}
    	if (maxCode <= 127) {
    		// pure ASCII String
    		if (len <= 16) {
                out.writeByte(SHORT_ASCII_STRING + len - 1);
    		} else {
    			out.writeByte(ASCII_STRING);
    			intOut(len);
    		}
    		for (int i = 0; i < len; ++i)
                out.writeByte((int)s.charAt(i));
    	} else if (maxCode < 2048) {
    		// UTF-8 out, with max. 2 byte sequences...
            out.writeByte(UTF8_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i) {
    			int c = (int)s.charAt(i);
    			if (c < 128) {
    				out.writeByte(c);
    			} else {
    				out.writeByte(0xC0 | (c >> 6));
    				out.writeByte(0x80 | (c & 0x3F));
    			}
    		}
    	} else {
    		// UTF-16, due to possible 3 byte sequences
            out.writeByte(UTF16_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i)
                out.writeChar((int)s.charAt(i));
    	}
    }
    
    // write a non-empty string (using char[])
    protected void writeLongStringArray(String s) throws IOException {
    	int maxCode = 0;
    	int len = s.length();
    	char buff [] = s.toCharArray();
    	for (int i = 0; i < len; ++ i) {
    		if (buff[i] > maxCode)
    			maxCode = buff[i];
    	}
    	if (maxCode <= 127) {
    		// pure ASCII String
    		if (len <= 16) {
                out.writeByte(SHORT_ASCII_STRING + len - 1);
    		} else {
    			out.writeByte(ASCII_STRING);
    			intOut(len);
    		}
    		for (int i = 0; i < len; ++i)
                out.writeByte(buff[i]);
    	} else if (maxCode < 2048) {
    		// UTF-8 out, with max. 2 byte sequences...
            out.writeByte(UTF8_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i) {
    			int c = (int)buff[i];
    			if (c < 128) {
    				out.writeByte(c);
    			} else {
    				out.writeByte(0xC0 | (c >> 6));
    				out.writeByte(0x80 | (c & 0x3F));
    			}
    		}
    	} else {
    		// UTF-16, due to possible 3 byte sequences
            out.writeByte(UTF16_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i)
                out.writeChar(buff[i]);
    	}
    }
    
    // write a non-empty string (using char[])
    protected void writeLongStringStealArray(String s) throws IOException {
    	if (unsafeString == null) {
    		writeLongStringArray(s);
    		return;
    	}
    	char buff[];
		try {
			buff = (char []) unsafeString.get(s);
		} catch (Exception e) {
    		writeLongStringArray(s);
    		return;
		}
    	int maxCode = 0;
    	int len = buff.length;
    	for (int i = 0; i < len; ++ i) {
    		if (buff[i] > maxCode)
    			maxCode = buff[i];
    	}
    	if (maxCode <= 127) {
    		// pure ASCII String
    		if (len <= 16) {
                out.writeByte(SHORT_ASCII_STRING + len - 1);
    		} else {
    			out.writeByte(ASCII_STRING);
    			intOut(len);
    		}
    		for (int i = 0; i < len; ++i)
                out.writeByte(buff[i]);
    	} else if (maxCode < 2048) {
    		// UTF-8 out, with max. 2 byte sequences...
            out.writeByte(UTF8_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i) {
    			int c = (int)buff[i];
    			if (c < 128) {
    				out.writeByte(c);
    			} else {
    				out.writeByte(0xC0 | (c >> 6));
    				out.writeByte(0x80 | (c & 0x3F));
    			}
    		}
    	} else {
    		// UTF-16, due to possible 3 byte sequences
            out.writeByte(UTF16_STRING);
    		intOut(len);
    		for (int i = 0; i < len; ++i)
                out.writeChar(buff[i]);
    	}
    }
    
    // output an integral value
    protected void intOut(int n) throws IOException {
        if (n >= 0) {
            // bisect the cases...
            if (n < 64) {
                // single byte
                if (n < 32)
                    out.writeByte(n);
                else
                    out.writeByte(0x80 - 32 + n);
            } else if (n <= 0x7fff) {
                if (n < 4096) {
                    // 2 byte integer
                    out.writeByte(0xc0 + (n >> 8));
                    out.writeByte(n);
                } else {
                    // 3 byte integer
                    out.writeByte(INT_2BYTE);
                    out.writeShort(n);
                }
            } else if (n <= 0x7fffff) {
                out.writeByte(INT_3BYTE);
                out.writeByte(n >> 16);
                out.writeShort(n);
            } else {
                out.writeByte(INT_4BYTE);
                out.writeInt(n);
            }
        } else {
            if (n >= -32768) {
                if (n >= -12)
                    out.writeByte(0xa0 - n);
                else {
                    out.writeByte(INT_2BYTE);
                    out.writeShort(n);
                }
            } else if (n >= -0x800000) {
                out.writeByte(INT_3BYTE);
                out.writeByte(n >> 16);
                out.writeShort(n);
            } else {
                out.writeByte(INT_4BYTE);
                out.writeInt(n);
            }
        }
    }
    
    protected void charOut(char c) throws IOException {
        // if it is a displayable ASCII character, there is a short form
        if (((int)c & ~0x7f) == 0 && (int)c >= 0x20) {
            // 1:1 mapping! write it as a byte!
            out.writeByte((int)c);
        } else {
            // something else. Write a single char
            out.writeByte(UNICODE_CHAR);
            out.writeShort(c);
        }
    }
    
    // character
    @Override
    public void addField(MiscElementaryDataItem di, char c) throws IOException {
    	charOut(c);
    }

    // ASCII only (unicode uses different method)
    @Override
    public void addField(AlphanumericElementaryDataItem di, String s) throws IOException {
        if (s == null) {
            writeNull();
        } else {
            if (s.length() == 0) {
                writeEmpty();
            } else if (s.length() == 1) {
            	charOut(s.charAt(0));
            } else if (s.length() > 8) {
            	writeLongStringStealArray(s);
            } else {
            	writeLongString(s);
            }
        }
    }

    // n is not null and not 0
    protected void bigintOut(BigInteger n) throws IOException {
        int l = n.bitLength();
        // see if we fit into an int
        if (l <= 31) {
            // yes, then store as an int
            intOut(n.intValue());   // intValueExact is Java 1.8
        } else {
            out.writeByte(COMPACT_BIGINTEGER);
            byte [] tmp = n.toByteArray();      // TODO: do some dirty trick to avoid temporary array construction!
            intOut(tmp.length);
            out.write(tmp);
        }
    }
    
    // decimal
    @Override
    public void addField(NumericElementaryDataItem di, BigDecimal n) throws IOException {
        if (n != null) {
            int sgn = n.signum();
            if (sgn == 0) {
                out.writeByte(0);
            } else {
                int scale = n.scale();
                if (scale > 0) {
                    // is a fractional number
                    if (scale <= 9) {
                        out.writeByte(COMPACT_BIGDECIMAL + scale);
                    } else {
                        out.writeByte(COMPACT_BIGDECIMAL);
                        intOut(scale);
                    }
                    bigintOut(n.unscaledValue());
                } else {
                    // number is integral
                    bigintOut(n.toBigInteger());
                }
            }
        } else {
            writeNull();
        }
    }

    // byte
    @Override
    public void addField(BasicNumericElementaryDataItem di, byte n) throws IOException {
        intOut(n);
    }

    // short
    @Override
    public void addField(BasicNumericElementaryDataItem di, short n) throws IOException {
        intOut(n);
    }

    // integer
    @Override
    public void addField(BasicNumericElementaryDataItem di, int n) throws IOException {
        intOut(n);
    }

    // int(n)
    @Override
    public void addField(BasicNumericElementaryDataItem di, Integer n) throws IOException {
        if (n != null) {
            intOut(n);
        } else {
            writeNull();
        }
    }

    // long
    @Override
    public void addField(BasicNumericElementaryDataItem di, long n) throws IOException {
        int nn = (int)n;
        if ((long)nn == n)
            intOut((int)n);
        else {
            out.writeByte(INT_8BYTE);  // TODO: optimize for 5, 6, 7 digits here!
            out.writeLong(n);
        }
    }

    // boolean
    @Override
    public void addField(MiscElementaryDataItem di, boolean b) throws IOException {
        out.writeByte(b ? 1 : 0);
    }

    // float
    @Override
    public void addField(BasicNumericElementaryDataItem di, float f) throws IOException {
        out.writeByte(COMPACT_FLOAT);
        out.writeFloat(f);
    }

    // double
    @Override
    public void addField(BasicNumericElementaryDataItem di, double d) throws IOException {
        out.writeByte(COMPACT_DOUBLE);
        out.writeDouble(d);
    }

    // UUID
    @Override
    public void addField(MiscElementaryDataItem di, UUID n) throws IOException {
        if (n != null) {
            out.writeByte(COMPACT_UUID);
            out.writeLong(n.getMostSignificantBits());
            out.writeLong(n.getLeastSignificantBits());
        } else {
            writeNull();
        }
    }

    // ByteArray: initial quick & dirty implementation
    @Override
    public void addField(BinaryElementaryDataItem di, ByteArray b) throws IOException {
        if (b != null) {
            if (b.length() == 0) {
                out.writeByte(EMPTY_FIELD);
            } else {
                out.writeByte(COMPACT_BINARY);
                intOut(b.length());
                b.writeToDataOutput(out);
            }
        } else {
            writeNull();
        }
    }

    // raw. Almost the same as ByteArray...
    @Override
    public void addField(BinaryElementaryDataItem di, byte[] b) throws IOException {
        if (b != null) {
            if (b.length == 0) {
                out.writeByte(EMPTY_FIELD);
            } else {
                out.writeByte(COMPACT_BINARY);
                intOut(b.length);
                out.write(b);
            }
        } else {
            writeNull();
        }
    }


    // converters for DAY und TIMESTAMP
    @Override
    public void addField(TemporalElementaryDataItem di, Calendar t) {
        throw new RuntimeException("Calendar will be scrapped soon");
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDate t) throws IOException {
        if (t != null) {
            out.writeByte(COMPACT_DATE);
            intOut(t.getYear());
            intOut(t.getMonthOfYear());
            intOut(t.getDayOfMonth());
        } else {
            writeNull();
        }
    }

    @Override
    public void addField(TemporalElementaryDataItem di, LocalDateTime t) throws IOException {
        if (t != null) {
            int millis = t.getMillisOfSecond();
            out.writeByte(millis == 0 ? COMPACT_DATETIME : COMPACT_DATETIME_MILLIS);
            intOut(t.getYear());
            intOut(t.getMonthOfYear());
            intOut(t.getDayOfMonth());
            intOut(t.getHourOfDay());
            intOut(t.getMinuteOfHour());
            intOut(t.getSecondOfMinute());
            if (millis != 0)
                intOut(millis);
        } else {
            writeNull();
        }
    }

    @Override
    public void startMap(FieldDefinition di, int currentMembers) throws IOException {
        out.writeByte(MAP_BEGIN);
        intOut(currentMembers);
    }

    @Override
    public void startArray(FieldDefinition di, int currentMembers, int sizeOfElement) throws IOException {
        out.writeByte(ARRAY_BEGIN);
        intOut(currentMembers);
    }

    @Override
    public void terminateArray() throws IOException {
        out.writeByte(COLLECTIONS_TERMINATOR);
    }

    @Override
    public void terminateMap() throws IOException {
        out.writeByte(COLLECTIONS_TERMINATOR);
    }

    @Override
    public void startObject(ObjectReference di, BonaPortable obj) throws IOException {
    	BonaPortableClass<?> meta = obj.get$BonaPortableClass();
    	if (recommendIdentifiable) {
    		out.writeByte(OBJECT_BEGIN_ID);
    		intOut(meta.getFactoryId());
    		intOut(meta.getId());
    	} else {
    		out.writeByte(OBJECT_BEGIN_PQON);
    		writeLongStringStealArray(meta.getPqon());
    		addField(REVISION_META, meta.getRevision());
    	}
    }

    @Override
    public void terminateObject(ObjectReference di, BonaPortable obj) throws IOException {
        out.writeByte(OBJECT_TERMINATOR);
    }

    @Override
    public void addField(ObjectReference di, BonaPortable obj) throws IOException {
        if (obj == null) {
            writeNull();
        } else {
            if (useCache) {
                Integer previousIndex = objectCache.get(obj);
                if (previousIndex != null) {
                    // reuse this instance
                    out.writeByte(OBJECT_AGAIN);
                    intOut(numberOfObjectsSerialized - previousIndex.intValue() - 1);
                    ++numberOfObjectReuses;
                    return;
                }
                // add the new object to the cache of known objects. This is
                // done despite we are not yet done with the object!
                objectCache.put(obj, Integer.valueOf(numberOfObjectsSerialized++));
                // fall through
            }
            // start a new object
            startObject(di, obj);
            // do all fields (now includes terminator)
            obj.serializeSub(this);
            // terminate the object
            terminateObject(di, obj);
        }
    }

    // enum with numeric expansion: delegate to Null/Int
    @Override
    public void addEnum(EnumDataItem di, BasicNumericElementaryDataItem ord, Enum<?> n) throws IOException {
        if (n == null)
            writeNull(ord);
        else
            addField(ord, n.ordinal());
    }

    // enum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(EnumDataItem di, AlphanumericElementaryDataItem token, TokenizableEnum n) throws IOException {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }

    // xenum with alphanumeric expansion: delegate to Null/String
    @Override
    public void addEnum(XEnumDataItem di, AlphanumericElementaryDataItem token, XEnum<?> n) throws IOException {
        if (n == null)
            writeNull(token);
        else
            addField(token, n.getToken());
    }
}