package de.jpaw.enums.examples;

import de.jpaw.enums.TokenizableEnum;

public enum AccountType1 implements TokenizableEnum {
    GL("G");
    private final String token;

    AccountType1(String token) {
        this.token = token;
    }
    @Override
    public String getToken() {
        return token;
    }
}

