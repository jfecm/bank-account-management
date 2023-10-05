package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateTransaction;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateTransaction;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.exceptions.*;
import com.jfecm.bankaccountmanagement.repository.AccountTransactionRepository;
import com.jfecm.bankaccountmanagement.repository.BankingAccountRepository;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BankingAccountServiceImpl implements BankingAccountService {
    private final ModelMapper mapper;
    private final BankingAccountRepository bankingAccountRepository;
    private final AccountTransactionRepository accountTransactionRepository;

    /**
     * Retrieves a banking account by its account number.
     *
     * @param accountNumber The account number to search for.
     * @return The banking account if found, otherwise throws a ResourceNotFoundException.
     */
    @Override
    public BankingAccount getBankingAccountByAccountNumber(String accountNumber) {
        return searchBankingAccountByAccountNumber(accountNumber);
    }

    /**
     * Retrieves a list of banking accounts based on their status.
     *
     * @param status The status of banking accounts to filter by.
     * @return A list of banking accounts with the specified status.
     * @throws InvalidStatusException if an invalid status is provided.
     */
    @Override
    public List<BankingAccount> getAllBankingAccounts(BankingAccountStatus status) {
        List<BankingAccount> accounts;
        accounts = bankingAccountRepository.findByBankingAccountStatus(status);
        log.info("Returning the list of accounts. List size: " + accounts.size());
        return accounts;
    }

    /**
     * Marks a banking account as closed by changing its status and setting the closing date.
     *
     * @param accountNumber The account number of the banking account to close.
     */
    @Override
    public void deleteBankingAccount(String accountNumber) {
        BankingAccount account = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(account);

        account.setBankingAccountStatus(BankingAccountStatus.CLOSED);
        account.setAccountClosingDate(LocalDate.now());

        bankingAccountRepository.save(account);

        log.info("deleteBankingAccount() OK banking account deleted.");
    }

    /**
     * Updates the status of a banking account by its account number.
     *
     * @param accountNumber    The account number of the banking account to update.
     * @param newAccountStatus The new status to set for the banking account.
     * @throws ResourceNotFoundException if the account is not found.
     * @throws InvalidStatusException    if an invalid status is provided.
     */
    @Override
    public void updateBankingAccountStatusByAccountNumber(String accountNumber, BankingAccountStatus newAccountStatus) {
        BankingAccount account = searchBankingAccountByAccountNumber(accountNumber);

        if (!newAccountStatus.equals(account.getBankingAccountStatus())) {
            account.setBankingAccountStatus(newAccountStatus);
            bankingAccountRepository.save(account);
            log.info("updateBankingAccountStatus() - OK.");
        }
    }

    /**
     * Retrieves a list of all transactions associated with a banking account.
     *
     * @param accountNumber The account number of the banking account.
     * @return A list of account transactions associated with the banking account.
     */
    @Override
    public List<AccountTransaction> getAllTransactionsByAccount(String accountNumber) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        return bankingAccount.getAccountTransactions();
    }

    /**
     * Retrieves a specific account transaction by its ID associated with a banking account.
     *
     * @param accountNumber The account number of the banking account.
     * @param idTransaction The ID of the transaction to retrieve.
     * @return The account transaction if found, otherwise throws a ResourceNotFoundException.
     */
    @Override
    public AccountTransaction getTransactionByAccountNumber(String accountNumber, Long idTransaction) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        List<AccountTransaction> accountTransactions = bankingAccount.getAccountTransactions();
        return accountTransactions.stream().filter(transaction -> transaction.getId().equals(idTransaction)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + idTransaction));
    }

    /**
     * Updates an existing account transaction by its ID associated with a banking account.
     *
     * @param accountNumber      The account number of the banking account.
     * @param idTransaction      The ID of the transaction to update.
     * @param accountTransaction Updated transaction data.
     * @return The updated account transaction.
     */
    @Override
    public AccountTransaction updateTransaction(String accountNumber, Long idTransaction, RequestUpdateTransaction accountTransaction) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        List<AccountTransaction> accountTransactions = bankingAccount.getAccountTransactions();

        AccountTransaction foundTransaction = accountTransactions.stream().filter(transaction -> transaction.getId().equals(idTransaction)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + idTransaction));

        mapper.map(accountTransaction, foundTransaction);
        foundTransaction.setTimeOfExecution(LocalTime.now());
        foundTransaction.setDateOfExecution(LocalDate.now());
        return accountTransactionRepository.save(foundTransaction);
    }

    /**
     * Deletes a specific account transaction by its ID associated with a banking account.
     *
     * @param accountNumber The account number of the banking account.
     * @param idTransaction The ID of the transaction to delete.
     */
    @Override
    public void deleteTransaction(String accountNumber, Long idTransaction) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        Optional<AccountTransaction> transaction = accountTransactionRepository.findById(idTransaction);

        if (transaction.isEmpty()) {
            throw new ResourceNotFoundException("Transaction not found with id " + idTransaction);
        }

        accountTransactionRepository.deleteById(idTransaction);
        log.info("deleteTransaction() - OK.");
    }

    /**
     * Filters account transactions of a banking account by transaction type.
     *
     * @param accountNumber The account number of the banking account.
     * @param type          The type of transaction to filter by.
     * @return A list of account transactions filtered by transaction type.
     */
    @Override
    public List<AccountTransaction> getAllTransactionsByType(String accountNumber, AccountTransactionType type) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        List<AccountTransaction> accountTransactions = bankingAccount.getAccountTransactions();

        return accountTransactions.stream().filter(transaction -> transaction.getAccountTransactionType() == type).collect(Collectors.toList());
    }

    /**
     * Filters account transactions of a banking account by date range.
     *
     * @param accountNumber The account number of the banking account.
     * @param fromDate      The start date of the date range.
     * @param toDate        The end date of the date range.
     * @return A list of account transactions filtered by date range.
     */
    @Override
    public List<AccountTransaction> getAllTransactionsByDateRange(String accountNumber, LocalDate fromDate, LocalDate toDate) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        List<AccountTransaction> accountTransactions = bankingAccount.getAccountTransactions();

        return accountTransactions.stream().filter(transaction -> {
            LocalDate transactionDate = transaction.getDateOfExecution();
            return !transactionDate.isBefore(fromDate) && !transactionDate.isAfter(toDate);
        }).collect(Collectors.toList());
    }

    /**
     * Filters account transactions of a banking account by transaction type and date range.
     *
     * @param accountNumber The account number of the banking account.
     * @param type          The type of transaction to filter by.
     * @param fromDate      The start date of the date range.
     * @param toDate        The end date of the date range.
     * @return A list of account transactions filtered by transaction type and date range.
     */
    @Override
    public List<AccountTransaction> getAllTransactionsByTypeAndDateRange(String accountNumber, AccountTransactionType type, LocalDate fromDate, LocalDate toDate) {
        BankingAccount bankingAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(bankingAccount);

        List<AccountTransaction> accountTransactions = bankingAccount.getAccountTransactions();

        return accountTransactions.stream().filter(transaction -> {
            LocalDate transactionDate = transaction.getDateOfExecution();
            return transaction.getAccountTransactionType() == type && !transactionDate.isBefore(fromDate) && !transactionDate.isAfter(toDate);
        }).collect(Collectors.toList());
    }

    /**
     * Recharges the balance of a banking account with a specified amount.
     *
     * @param accountNumber The account number of the banking account.
     * @param amount        The amount to recharge.
     * @return The account transaction representing the recharge.
     */
    @Override
    public AccountTransaction rechargeAccountBalance(String accountNumber, Double amount) {
        checkAmount(amount);

        BankingAccount account = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(account);

        account.setBalance(account.getBalance() + amount);
        AccountTransaction transferTransaction = buildTransaction(account, AccountTransactionType.RECHARGE, amount);
        account.getAccountTransactions().add(transferTransaction);

        return accountTransactionRepository.save(transferTransaction);
    }

    /**
     * Creates a withdrawal transaction from a banking account with a specified amount.
     *
     * @param accountNumber The account number of the banking account.
     * @param amount        The amount to withdraw.
     * @return The account transaction representing the withdrawal.
     */
    @Override
    public AccountTransaction createWithdrawalTransaction(String accountNumber, Double amount) {
        checkAmount(amount);

        BankingAccount account = searchBankingAccountByAccountNumber(accountNumber);

        checkAccountStatus(account);
        checkWithdrawalLimit(account, amount);
        checkFunds(account.getBalance(), amount);

        account.setBalance(account.getBalance() - amount);
        AccountTransaction transferTransaction = buildTransaction(account, AccountTransactionType.WITHDRAWAL, amount);
        account.getAccountTransactions().add(transferTransaction);

        return accountTransactionRepository.save(transferTransaction);
    }

    /**
     * Creates a transfer transaction between banking accounts.
     *
     * @param accountNumber The source account number.
     * @param transaction   The transfer transaction details.
     * @return The account transaction representing the transfer.
     */
    @Override
    public AccountTransaction createTransferTransaction(String accountNumber, RequestCreateTransaction transaction) {
        checkAmount(transaction.getAmount());
        Double transferAmount = transaction.getAmount();

        BankingAccount sourceAccount = searchBankingAccountByAccountNumber(accountNumber);
        checkAccountStatus(sourceAccount);
        checkFunds(sourceAccount.getBalance(), transferAmount);
        checkWithdrawalLimit(sourceAccount, transferAmount);

        BankingAccount destinationAccount = searchBankingAccountByAccountNumber(transaction.getDestinationAccountNumber());
        checkAccountStatus(destinationAccount);

        if (sourceAccount.getAccountNumber().equals(destinationAccount.getAccountNumber())) {
            throw new InvalidTransactionException("Cannot make a transfer into the same account.");
        }

        AccountTransaction sourceTransfer = buildTransaction(sourceAccount, AccountTransactionType.TRANSFER, transferAmount);
        AccountTransaction destinationTransfer = buildTransaction(destinationAccount, AccountTransactionType.TRANSFER, transferAmount);

        sourceAccount.setBalance(sourceAccount.getBalance() - transferAmount);
        destinationAccount.setBalance(destinationAccount.getBalance() + transferAmount);

        accountTransactionRepository.save(sourceTransfer);
        accountTransactionRepository.save(destinationTransfer);

        return sourceTransfer;
    }

    /**
     * Builds a new account transaction with the provided details.
     *
     * @param account                The banking account associated with the transaction.
     * @param accountTransactionType The type of account transaction.
     * @param amount                 The transaction amount.
     * @return The newly created account transaction.
     */
    private AccountTransaction buildTransaction(BankingAccount account, AccountTransactionType accountTransactionType, Double amount) {
        return AccountTransaction.builder().amount(amount).accountTransactionType(accountTransactionType).dateOfExecution(LocalDate.now()).timeOfExecution(LocalTime.now()).bankingAccount(account).build();
    }

    /**
     * Searches for a banking account by its account number.
     *
     * @param accountNumber The account number to search for.
     * @return The banking account if found, otherwise throws a ResourceNotFoundException.
     */
    private BankingAccount searchBankingAccountByAccountNumber(String accountNumber) {
        BankingAccount account = bankingAccountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new ResourceNotFoundException("Account not found with account number: " + accountNumber);
        }

        return account;
    }

    /**
     * Checks if there are sufficient funds to cover a transaction.
     *
     * @param currentAmount The current amount in the account.
     * @param requestAmount The requested transaction amount.
     * @throws InsufficientFundsException if there are not enough funds for the transaction.
     */
    private void checkFunds(Double currentAmount, Double requestAmount) {
        // The request must be less than or equal to the current balance to be processed successfully.
        if (requestAmount > currentAmount) {
            throw new InsufficientFundsException("Insufficient balance in the source account.");
        }
    }

    /**
     * Checks if a withdrawal transaction exceeds the account's withdrawal limit.
     *
     * @param account The banking account.
     * @param amount  The withdrawal amount.
     * @throws InsufficientFundsException if the withdrawal exceeds the account's withdrawal limit.
     */
    private void checkWithdrawalLimit(BankingAccount account, Double amount) {
        Double withdrawalLimit = account.getWithdrawalLimit();

        if (amount > withdrawalLimit) {
            throw new InsufficientFundsException("Exceeded withdrawal limit. Withdrawal limit: " + withdrawalLimit);
        }

    }

    /**
     * Checks the status of a banking account and ensures it is active.
     *
     * @param account The banking account to check.
     * @throws InactiveAccountException if the account is not active.
     */
    private void checkAccountStatus(BankingAccount account) {
        if (account.getBankingAccountStatus() != BankingAccountStatus.ACTIVE) {
            throw new InactiveAccountException("The bank account is not active.");
        }
    }

    /**
     * Checks if the transaction amount is positive.
     *
     * @param amount The transaction amount to check.
     * @throws InvalidTransactionException if the amount is not positive.
     */
    private void checkAmount(Double amount) {
        if (amount <= 0) {
            throw new InvalidTransactionException("The amount must be positive.");
        }
    }
}
