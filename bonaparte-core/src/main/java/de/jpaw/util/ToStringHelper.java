package de.jpaw.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import de.jpaw.bonaparte.core.BonaPortable;

public class ToStringHelper {
    
    public static String toStringML(BonaPortable obj) {
        StringBuilder _buffer = new StringBuilder(1000);
        BonaPortable(_buffer, new StringBuilder("\n"), true, obj);
        return _buffer.toString();
    }
    
    public static String toStringSL(BonaPortable obj) {
        StringBuilder _buffer = new StringBuilder(1000);
        BonaPortable(_buffer, null, false, obj);
        return _buffer.toString();
    }

    private static void delimiter(StringBuilder _buffer, StringBuilder _currentIndent, boolean ofSuperClass) {
        if (_currentIndent == null) {
            // single line output
            _buffer.append(ofSuperClass ? "< " : ", ");
        } else {
            if (ofSuperClass) {
                _buffer.append(_currentIndent);
                _buffer.append("^^^");
                _buffer.append(_currentIndent);
            } else {
                _buffer.append(",");
                _buffer.append(_currentIndent);
            }
        }
    }
    
    // returns true if at leastone field has been printed
    private static boolean BonaPortableSub(StringBuilder _buffer, StringBuilder _currentIndent, boolean showNulls, BonaPortable obj,
            Class<?> thisClass) {
        boolean firstField = true;
        boolean didSome = false;
        if (thisClass.getSuperclass() != Object.class) {
            // descend
            didSome = BonaPortableSub(_buffer, _currentIndent, showNulls, obj, thisClass.getSuperclass());
            if (didSome)
                delimiter(_buffer, _currentIndent, true);
        }
        for (Field field : thisClass.getDeclaredFields()) {
            if ((field.getModifiers() & Modifier.STATIC) != 0)
                continue;           // skip static fields
            if (!firstField)
                delimiter(_buffer, _currentIndent, false);
            field.setAccessible(true); // You might want to set modifier to public first.
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalArgumentException e) {
                _buffer.append(field.getName());
                _buffer.append(": ***Illegal argument exception***");
                firstField = false;
            } catch (IllegalAccessException e) {
                _buffer.append(field.getName());
                _buffer.append(": ***Illegal access exception***");
                firstField = false;
            }
            if (value == null) {
                if (showNulls) {
                    _buffer.append(field.getName());
                    _buffer.append("=null");
                    firstField = false;
                }
            } else {
                firstField = false;
                _buffer.append(field.getName());
                _buffer.append("=");
                // catch some special cases, do the rest generic
                if (value instanceof BonaPortable) {
                    // a sub-object, recursively call this method
                    BonaPortable(_buffer, _currentIndent, showNulls, (BonaPortable)value);
                } else if (value instanceof java.util.List) {
                    // output a list of objects
                    boolean firstInList = true;
                    _buffer.append("[");
                    for (Object e : (java.util.List<?>)value) {
                        if (!firstInList)
                            _buffer.append(", ");
                        firstInList = false;
                        if (e == null) {
                            _buffer.append("null");
                        } else if (e instanceof BonaPortable) {
                            BonaPortable(_buffer, _currentIndent, showNulls, (BonaPortable)e);
                        } else {
                            _buffer.append(e.toString());
                        }
                    }
                    _buffer.append("]");
                } else if (value instanceof java.util.Set) {
                    // output a list of objects
                    boolean firstInList = true;
                    _buffer.append("{");
                    for (Object e : (java.util.Set<?>)value) {
                        if (!firstInList)
                            _buffer.append(", ");
                        firstInList = false;
                        if (e == null) {
                            _buffer.append("null");
                        } else if (e instanceof BonaPortable) {
                            BonaPortable(_buffer, _currentIndent, showNulls, (BonaPortable)e);
                        } else {
                            _buffer.append(e.toString());
                        }
                    }
                    _buffer.append("}");
                } else if (value instanceof java.util.Map) {
                    // output a map of objects
                    boolean firstInList = true;
                    _buffer.append("(");
                    Map<?,?> m = (java.util.Map<?,?>)value;
                    for (Map.Entry<?,?> e : m.entrySet()) {
                        if (!firstInList)
                            _buffer.append(", ");
                        firstInList = false;
                        _buffer.append(e.getKey().toString());
                        _buffer.append(":");
                        Object v = e.getValue();
                        if (v == null) {
                            _buffer.append("null");
                        } else if (v instanceof BonaPortable) {
                            BonaPortable(_buffer, _currentIndent, showNulls, (BonaPortable)v);
                        } else {
                            _buffer.append(v.toString());
                        }
                    }
                    _buffer.append(")");
                } else {
                    _buffer.append(value.toString());
                }
            }
        }
        return didSome || !firstField;
    }

    public static void BonaPortable(StringBuilder _buffer, StringBuilder _currentIndent, boolean showNulls, BonaPortable obj) {
        _buffer.append(obj.get$PQON());
        _buffer.append("(");
        if (_currentIndent != null) {
            _currentIndent.append("  ");                                // indent more
            _buffer.append(_currentIndent);
        }
        // object output
        // this is mainly used for debugging, so speed is not as relevant and reflection can be used instead of generated code
        BonaPortableSub(_buffer, _currentIndent, showNulls, obj, obj.getClass());
        // closure
        if (_currentIndent != null) {
            _currentIndent.setLength(_currentIndent.length() - 2);      // restore previous length
            _buffer.append(_currentIndent);                             // and add for closing parenthesis
        }
        _buffer.append(")");
    }

}
