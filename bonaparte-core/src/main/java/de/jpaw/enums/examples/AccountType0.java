package de.jpaw.enums.examples;

import de.jpaw.enums.TokenizableEnum;

public enum AccountType0 implements TokenizableEnum {
    DEBTOR("D"), CREDITOR("C");
    private final String token;
    
    AccountType0(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
