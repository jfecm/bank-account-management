package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.builders.BankingAccountBuilder;
import com.jfecm.bankaccountmanagement.dto.request.RequestCreateTransaction;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateTransaction;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.exceptions.InactiveAccountException;
import com.jfecm.bankaccountmanagement.exceptions.InvalidTransactionException;
import com.jfecm.bankaccountmanagement.exceptions.ResourceNotFoundException;
import com.jfecm.bankaccountmanagement.repository.AccountTransactionRepository;
import com.jfecm.bankaccountmanagement.repository.BankingAccountRepository;
import com.jfecm.bankaccountmanagement.service.imp.BankingAccountServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("BankingAccountServiceImpl Tests")
@ExtendWith(MockitoExtension.class)
class BankingAccountServiceImplTest {
    @Mock
    private BankingAccountRepository bankingAccountRepository;
    @Mock
    private AccountTransactionRepository accountTransactionRepository;
    @InjectMocks
    private BankingAccountServiceImpl bankingAccountService;
    @Mock
    private ModelMapper mapper;
    private BankingAccount bankingAccount;

    @BeforeEach
    void setUp() {
        bankingAccount = BankingAccountBuilder.buildBankingAccountService();
    }

    @AfterEach
    void tearDown() {
        bankingAccountRepository.delete(bankingAccount);
    }

    @Test @DisplayName("Given an account number, when getting a banking account, then return the banking account")
    void givenAccountNumber_whenGetBankingAccountByAccountNumber_thenReturnBankingAccount() {
        String accountNumber = bankingAccount.getAccountNumber();
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        BankingAccount result = bankingAccountService.getBankingAccountByAccountNumber(accountNumber);

        assertNotNull(result);
        assertEquals(bankingAccount.getAccountNumber(), result.getAccountNumber());
    }

    @Test @DisplayName("Given an invalid account number, when getting a banking account, then throw ResourceNotFoundException")
    void givenAccountNumber_whenGetBankingAccountByAccountNumber_thenThrowResourceNotFoundException() {
        String accountNumber = bankingAccount.getAccountNumber();
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(null);

        ResourceNotFoundException result = assertThrows(ResourceNotFoundException.class, () ->
                bankingAccountService.getBankingAccountByAccountNumber(accountNumber));

        assertEquals("Account not found with account number: " + accountNumber, result.getMessage());
    }

    @Test @DisplayName("Given banking account status, when getting all banking accounts, then return an banking account list")
    void givenBankingAccountStatus_whenGetAllBankingAccounts_thenReturnBankingAccountList() {
        BankingAccountStatus status = BankingAccountStatus.ACTIVE;
        List<BankingAccount> bankingAccountList = List.of(
                BankingAccountBuilder.buildBankingAccountService(),
                BankingAccountBuilder.buildBankingAccountService(),
                BankingAccountBuilder.buildBankingAccountService(),
                BankingAccountBuilder.buildBankingAccountService()
        );
        when(bankingAccountRepository.findByBankingAccountStatus(status)).thenReturn(bankingAccountList);

        List<BankingAccount> result = bankingAccountService.getAllBankingAccounts(status);

        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test @DisplayName("Given banking account status, when getting all banking accounts, then return a empty banking account list")
    void givenBankingAccountStatus_whenGetAllBankingAccounts_thenReturnEmptyBankingAccountList() {
        BankingAccountStatus status = BankingAccountStatus.INACTIVE;
        when(bankingAccountRepository.findByBankingAccountStatus(status)).thenReturn(new ArrayList<>());

        List<BankingAccount> result = bankingAccountService.getAllBankingAccounts(status);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test @DisplayName("Given an account number, when deleting a banking account, then delete the banking account")
    void givenAccountNumber_whenDeleteBankingAccount_thenDeleteBankingAccount() {
        String accountNumber = bankingAccount.getAccountNumber();
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);
        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(bankingAccount);

        bankingAccountService.deleteBankingAccount(accountNumber);

        verify(bankingAccountRepository, times(1)).save(bankingAccount);
        assertEquals(BankingAccountStatus.CLOSED, bankingAccount.getBankingAccountStatus());
    }

    @Test @DisplayName("Given an inactive account, when deleting a banking account, then throw InactiveAccountException")
    void givenAccountNumber_whenDeleteBankingAccount_thenThrowInactiveAccountException() {
        BankingAccount bankingAccountDisabled = BankingAccount.builder().accountNumber("123").bankingAccountStatus(BankingAccountStatus.INACTIVE).build();
        when(bankingAccountRepository.findByAccountNumber(anyString())).thenReturn(bankingAccountDisabled);

        InactiveAccountException result = assertThrows(InactiveAccountException.class, () ->
                bankingAccountService.deleteBankingAccount(anyString()));

        assertEquals("The bank account is not active.", result.getMessage());
    }

    @Test @DisplayName("Given an account number and new status, when updating banking account status, then update the status")
    void givenAccountNumberAndAccountStatus_whenUpdateBankingAccountStatusByAccountNumber_thenUpdateBankingAccountStatus() {
        BankingAccountStatus newStatus = BankingAccountStatus.FROZEN;
        String accountNumber = bankingAccount.getAccountNumber();
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);
        when(bankingAccountRepository.save(any(BankingAccount.class))).thenReturn(bankingAccount);

        bankingAccountService.updateBankingAccountStatusByAccountNumber(accountNumber, newStatus);

        verify(bankingAccountRepository, times(1)).save(bankingAccount);
        assertEquals(newStatus, bankingAccount.getBankingAccountStatus());
    }

    @Test @DisplayName("Given an account number, when getting all transactions by account, then return a list of transactions")
    void givenAccountNumber_whenGetAllTransactionsByAccount_thenReturnAllTransactions() {
        String accountNumber = bankingAccount.getAccountNumber();
        List<AccountTransaction> accountTransactionList = List.of(
                new AccountTransaction(),
                new AccountTransaction(),
                new AccountTransaction(),
                new AccountTransaction()
        );
        bankingAccount.setAccountTransactions(accountTransactionList);
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        List<AccountTransaction> result = bankingAccountService.getAllTransactionsByAccount(accountNumber);

        assertNotNull(result);
        assertEquals(4, result.size());
    }

    @Test @DisplayName("Given an account number and id transaction, when getting a transaction, then return the transaction")
    void givenAccountNumberAndIdTransaction_whenGetTransactionByAccountNumber_thenReturnAccountTransaction() {
        String accountNumber = bankingAccount.getAccountNumber();
        List<AccountTransaction> accountTransactionList = List.of(
                AccountTransaction.builder().id(1L).build(),
                AccountTransaction.builder().id(2L).build(),
                AccountTransaction.builder().id(3L).build(),
                AccountTransaction.builder().id(4L).build()
        );
        bankingAccount.setAccountTransactions(accountTransactionList);
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        AccountTransaction result = bankingAccountService.getTransactionByAccountNumber(accountNumber, 3L);

        assertNotNull(result);
        assertEquals(3L, result.getId());
    }

    @Test @DisplayName("Given an account number, id transaction and a transaction update request, when updating a transaction, then update the transaction")
    void givenAccountNumberIdTransactionAndRequestUpdateTransaction_whenUpdateTransaction_thenReturnUpdatedAccountTransaction() {
        String accountNumber = bankingAccount.getAccountNumber();
        List<AccountTransaction> accountTransactionList = List.of(AccountTransaction.builder().id(1L).build());
        bankingAccount.setAccountTransactions(accountTransactionList);
        RequestUpdateTransaction requestUpdateTransaction = RequestUpdateTransaction.builder().amount(100.0).build();
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);
        when(accountTransactionRepository.save(any(AccountTransaction.class))).thenReturn(AccountTransaction.builder().id(1L).amount(100.0).build());

        AccountTransaction result = bankingAccountService.updateTransaction(accountNumber, 1L, requestUpdateTransaction);

        verify(accountTransactionRepository, times(1)).save(any(AccountTransaction.class));
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100.0, result.getAmount());
    }

    @Test @DisplayName("Given a transaction, when deleting a transaction, then delete the transaction")
    void givenAccountNumberAndIdTransaction_whenDeleteTransaction_thenDeleteAccountTransaction() {
        String accountNumber = bankingAccount.getAccountNumber();
        Long idTransaction = 1L;
        List<AccountTransaction> accountTransactionList = List.of(
                AccountTransaction.builder().id(1L).build(),
                AccountTransaction.builder().id(2L).build(),
                AccountTransaction.builder().id(3L).build(),
                AccountTransaction.builder().id(4L).build()
        );
        bankingAccount.setAccountTransactions(accountTransactionList);
        AccountTransaction transactionToDelete = new AccountTransaction();
        transactionToDelete.setId(idTransaction);

        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);
        when(accountTransactionRepository.findById(idTransaction)).thenReturn(Optional.of(transactionToDelete));

        bankingAccountService.deleteTransaction(accountNumber, idTransaction);

        verify(accountTransactionRepository, times(1)).deleteById(idTransaction);
    }

    @Test @DisplayName("Given an account number and transaction type, when getting all transactions by type, then return a list of transactions")
    void givenAccountNumberAndAccountTransactionType_whenGetAllTransactionsByType_thenReturnAccountTransactionList() {
        String accountNumber = bankingAccount.getAccountNumber();
        AccountTransactionType typeToFilter = AccountTransactionType.RECHARGE;
        List<AccountTransaction> accountTransactions = List.of(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.RECHARGE).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.WITHDRAWAL).build(),
                AccountTransaction.builder().id(3L).accountTransactionType(AccountTransactionType.RECHARGE).build(),
                AccountTransaction.builder().id(4L).accountTransactionType(AccountTransactionType.WITHDRAWAL).build()
        );
        bankingAccount.setAccountTransactions(accountTransactions);
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        List<AccountTransaction> result = bankingAccountService.getAllTransactionsByType(accountNumber, typeToFilter);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test @DisplayName("Given a date range, when getting all transactions by date range, then return a list of transactions")
    void givenAccountNumberAndDateRange_whenGetAllTransactionsByDateRange_thenReturnAccountTransactionList() {
        String accountNumber = bankingAccount.getAccountNumber();
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        List<AccountTransaction> accountTransactions = List.of(
                AccountTransaction.builder().id(1L).dateOfExecution(LocalDate.of(2023, 2, 15)).build(),
                AccountTransaction.builder().id(2L).dateOfExecution(LocalDate.of(2023, 6, 30)).build(),
                AccountTransaction.builder().id(3L).dateOfExecution(LocalDate.of(2023, 11, 10)).build()
        );
        bankingAccount.setAccountTransactions(accountTransactions);
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        List<AccountTransaction> result = bankingAccountService.getAllTransactionsByDateRange(accountNumber, fromDate, toDate);

        assertEquals(3, result.size());
    }

    @Test @DisplayName("Given a transaction type and date range, when getting all transactions by type and date range, then return a list of transactions")
    void givenAccountNumberAndAccountTransactionTypeAndDateRange_whenGetAllTransactionsByTypeAndDateRange_thenReturnAccountTransactionList() {
        String accountNumber = bankingAccount.getAccountNumber();
        AccountTransactionType typeToFilter = AccountTransactionType.TRANSFER;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        List<AccountTransaction> accountTransactions = List.of(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.TRANSFER).dateOfExecution(LocalDate.of(2023, 2, 15)).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.WITHDRAWAL).dateOfExecution(LocalDate.of(2023, 6, 30)).build(),
                AccountTransaction.builder().id(3L).accountTransactionType(AccountTransactionType.TRANSFER).dateOfExecution(LocalDate.of(2023, 11, 10)).build()
        );
        bankingAccount.setAccountTransactions(accountTransactions);
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        List<AccountTransaction> result = bankingAccountService.getAllTransactionsByTypeAndDateRange(accountNumber, typeToFilter, fromDate, toDate);

        assertEquals(2, result.size());
    }

    @Test @DisplayName("Given an invalid amount, when recharging an account balance, then throw InvalidTransactionException")
    void givenInvalidAmount_whenRechargeAccountBalance_thenThrowInvalidTransactionException() {
        String accountNumber = bankingAccount.getAccountNumber();
        Double invalidAmount = -50.0;

        assertThrows(InvalidTransactionException.class, () -> bankingAccountService.rechargeAccountBalance(accountNumber, invalidAmount));
    }

    @Test @DisplayName("Given an amount to recharge, when recharging an account balance, then update the balance")
    void givenAccountNumberAndAmountToRecharge_whenRechargeAccountBalance_thenUpdateBalance() {
        String accountNumber = bankingAccount.getAccountNumber();
        Double amountToRecharge = 100.0;
        bankingAccount.setBalance(500.0);
        bankingAccount.setAccountTransactions(new ArrayList<>());
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        bankingAccountService.rechargeAccountBalance(accountNumber, amountToRecharge);

        assertEquals(600.0, bankingAccount.getBalance());
        verify(accountTransactionRepository, times(1)).save(any(AccountTransaction.class));
    }

    @Test @DisplayName("Given an amount to withdraw, when creating a withdrawal transaction, then update the balance")
    void givenAccountNumberAndWithdrawalAmount_whenCreateWithdrawalTransaction_thenUpdateBalance() {
        String accountNumber = bankingAccount.getAccountNumber();
        Double withdrawalAmount = 50.0;
        bankingAccount.setBalance(500.0);
        bankingAccount.setWithdrawalLimit(5000.0);
        bankingAccount.setAccountTransactions(new ArrayList<>());
        when(bankingAccountRepository.findByAccountNumber(accountNumber)).thenReturn(bankingAccount);

        bankingAccountService.createWithdrawalTransaction(accountNumber, withdrawalAmount);

        assertEquals(450.0, bankingAccount.getBalance());
        verify(accountTransactionRepository, times(1)).save(any(AccountTransaction.class));
    }

    @Test @DisplayName("Given a source account, destination account, and transfer amount, when creating a transfer transaction, then update the balances")
    void givenValidAccounts_whenPerformSuccessfulTransfer_thenUpdateBalances() {
        String sourceAccountNumber = "123";
        String destinationAccountNumber = "321";
        Double transferAmount = 100.0;

        RequestCreateTransaction requestCreateTransaction = RequestCreateTransaction.builder().destinationAccountNumber(destinationAccountNumber).amount(transferAmount).build();

        BankingAccount sourceAccount = new BankingAccount();
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(500.0);
        sourceAccount.setBankingAccountStatus(BankingAccountStatus.ACTIVE);
        sourceAccount.setWithdrawalLimit(5000.0);

        BankingAccount destinationAccount = new BankingAccount();
        destinationAccount.setAccountNumber(destinationAccountNumber);
        destinationAccount.setBalance(300.0);
        destinationAccount.setBankingAccountStatus(BankingAccountStatus.ACTIVE);
        destinationAccount.setWithdrawalLimit(5000.0);

        when(bankingAccountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(sourceAccount);
        when(bankingAccountRepository.findByAccountNumber(destinationAccountNumber)).thenReturn(destinationAccount);

        bankingAccountService.createTransferTransaction(sourceAccountNumber, requestCreateTransaction);

        assertEquals(400.0, sourceAccount.getBalance());
        assertEquals(400.0, destinationAccount.getBalance());

        verify(accountTransactionRepository, times(2)).save(any(AccountTransaction.class));
    }
}