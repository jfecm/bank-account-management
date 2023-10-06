package com.jfecm.bankaccountmanagement.builders;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;

import java.time.LocalDate;
import java.util.UUID;

public class BankingAccountBuilder {
    public static BankingAccount buildBankingAccount() {
        return BankingAccount.builder()
                .accountNumber(getRandomAccountNumber())
                .balance(0.0)
                .withdrawalLimit(0.0)
                .accountOpenedDate(LocalDate.now())
                .accountClosingDate(LocalDate.now())
                .bankingAccountStatus(BankingAccountStatus.ACTIVE)
                .build();
    }

    public static String getRandomAccountNumber() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        // Get the UUID as a string and remove any dashes
        return uuid.toString().replace("-", "");
    }
}
