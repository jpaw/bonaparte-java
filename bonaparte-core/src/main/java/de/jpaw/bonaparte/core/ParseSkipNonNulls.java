package de.jpaw.bonaparte.core;

/** Defines the possible behaviours when parsing input data and encountering extra (non-Null) data at expected end of record. */
public enum ParseSkipNonNulls {
    WARN,       // parse the record, but emit a warning message
    ERROR,      // throw an exception
    IGNORE      // parse the record (skip data until end of message part without a warning)
}
