package com.jfecm.bankaccountmanagement.entity.enums;

/**
 * Enumerates the possible statuses of a banking account.
 * ACTIVE   : The banking account is active and in good standing.
 * BLOCKED  : The banking account is blocked.
 * INACTIVE : The banking account is inactive and not in use.
 * CLOSED   : The banking account has been closed.
 * OVERDUE  : The banking account is overdue or past due.
 * FROZEN   : The banking account has been frozen.
 */
public enum BankingAccountStatus {
    ACTIVE("Active"),
    BLOCKED("Blocked"),
    INACTIVE("Inactive"),
    CLOSED("Closed"),
    OVERDUE("Overdue"),
    FROZEN("Frozen");

    private final String displayValue;

    BankingAccountStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
