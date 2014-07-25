package de.jpaw.bonaparte.core;

import de.jpaw.enums.TokenizableEnum;

/** Style for Date and time output, used in CSV output configuration. */
public enum CSVStyle implements TokenizableEnum {
    SHORT("S"), MEDIUM("M"), LONG("L"), FULL("F");

    // constructor by token
    private String _token;

    private CSVStyle(String _token) {
        this._token = _token;
    }

    static public int getMaxTokenLength() {
        return 5;
    }

    // token retrieval
    @Override
    public String getToken() {
        return _token;
    }
}
