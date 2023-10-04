package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateTransaction;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateTransaction;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;

import java.time.LocalDate;
import java.util.List;

public interface BankingAccountService {
    BankingAccount getBankingAccountByAccountNumber(String accountNumber);

    List<BankingAccount> getAllBankingAccounts(String accountStatus);

    void deleteBankingAccount(String accountNumber);

    void updateBankingAccountStatusByAccountNumber(String accountNumber, String newAccountStatus);

    AccountTransaction rechargeAccountBalance(String accountNumber, Double amount);

    AccountTransaction createWithdrawalTransaction(String accountNumber, Double amount);

    AccountTransaction createTransferTransaction(String accountNumber, RequestCreateTransaction transaction);

    List<AccountTransaction> getAllTransactionsByAccount(String accountNumber);

    List<AccountTransaction> getAllTransactionsByType(String accountNumber, AccountTransactionType type);

    List<AccountTransaction> getAllTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate);

    List<AccountTransaction> getAllTransactionsByTypeAndDateRange(String accountNumber, AccountTransactionType type, LocalDate fromDate, LocalDate toDate);

    AccountTransaction getTransactionByAccountNumber(String accountNumber, Long idTransaction);

    AccountTransaction updateTransaction(String accountNumber, Long idTransaction, RequestUpdateTransaction accountTransaction);

    void deleteTransaction(String accountNumber, Long idTransaction);
}
