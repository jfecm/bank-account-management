package com.jfecm.bankaccountmanagement.entity.enums;

/**
 * ACTIVE  : Represents an active user.
 * INACTIVE: Represents an inactive user.
 * PENDING : Represents a user whose account is pending activation.
 * BANNED  : Represents a user who has been banned or suspended.
 */
public enum UserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    PENDING("Pending"),
    BANNED("Banned");

    private final String displayValue;

    UserStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
