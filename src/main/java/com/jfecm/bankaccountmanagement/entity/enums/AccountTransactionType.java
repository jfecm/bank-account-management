package com.jfecm.bankaccountmanagement.entity.enums;

/**
 * Enumerates the possible types of account transactions.
 * - RECHARGE   : Represents a deposit transaction.
 * - WITHDRAWAL : Represents a withdrawal transaction.
 * - TRANSFER   : Represents a transfer transaction.
 * - INTEREST   : Represents an interest transaction.
 * - FEE        : Represents a fee transaction.
 */
public enum AccountTransactionType {
    RECHARGE("Recharge"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER("Transfer");

    private final String displayValue;

    AccountTransactionType(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
