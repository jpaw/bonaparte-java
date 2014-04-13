package de.jpaw.enums;

/** The interface implemented by all "regular" Java Bonaparte enums, using a token as an abbreviated string to store the value. */
public interface TokenizableEnum {
    // list a few required standard Java methods and the getToken() one
    String getToken();
    
    // standard Java enum methods:
    String name();
    int ordinal();
}
