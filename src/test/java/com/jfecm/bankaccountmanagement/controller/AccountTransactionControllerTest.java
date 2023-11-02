package com.jfecm.bankaccountmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfecm.bankaccountmanagement.dto.request.RequestCreateTransaction;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateTransaction;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("AccountTransactionController Tests")
@WebMvcTest(AccountTransactionController.class)
class AccountTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankingAccountService bankingAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test @DisplayName("Given an account number and an amount, when recharge is called, then recharge the account balance")
    void givenAccountNumberAndAmount_whenRecharge_thenReturnRechargeTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/recharge/{amount}";
        String accountNumber = "123456789";
        Double amount = 100.0;
        AccountTransaction depositTransaction = new AccountTransaction();
        when(bankingAccountService.rechargeAccountBalance(accountNumber, amount)).thenReturn(depositTransaction);

        mockMvc.perform(post(urlTemplate, accountNumber, amount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value(depositTransaction))
                .andExpect(jsonPath("$.Result.amount").value(depositTransaction.getAmount()));

        verify(bankingAccountService, times(1)).rechargeAccountBalance(accountNumber, amount);
    }

    @Test @DisplayName("Given account number and withdrawal amount, when withdrawal is called, then create withdrawal transaction")
    void givenAccountNumberAndAmount_whenWithdrawal_thenReturnWithdrawalTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/withdrawal/{amount}";
        String accountNumber = "123456789";
        Double withdrawalAmount = 50.0;
        AccountTransaction withdrawalTransaction = AccountTransaction.builder()
                .id(1L)
                .amount(withdrawalAmount)
                .accountTransactionType(AccountTransactionType.WITHDRAWAL).build();
        when(bankingAccountService.createWithdrawalTransaction(accountNumber, withdrawalAmount))
                .thenReturn(withdrawalTransaction);

        mockMvc.perform(post(urlTemplate, accountNumber, withdrawalAmount))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id", is(1)))
                .andExpect(jsonPath("$.Result.amount", is(50.0)))
                .andExpect(jsonPath("$.Result.accountTransactionType", is("WITHDRAWAL")));

        verify(bankingAccountService, times(1)).createWithdrawalTransaction(accountNumber, withdrawalAmount);
    }

    @Test @DisplayName("Given account number and transfer request, when transfer is called, then create transfer transaction")
    void givenAccountNumberAndRequestCreateTransaction_whenTransfer_thenReturnTransferTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/transfer";
        String accountNumber = "123456789";
        RequestCreateTransaction requestTransfer = RequestCreateTransaction.builder()
                .amount(100.0)
                .destinationAccountNumber("987654321")
                .build();

        AccountTransaction transferTransaction = AccountTransaction.builder()
                .id(1L)
                .amount(100.0)
                .accountTransactionType(AccountTransactionType.TRANSFER)
                .build();

        when(bankingAccountService.createTransferTransaction(accountNumber, requestTransfer))
                .thenReturn(transferTransaction);

        mockMvc.perform(post(urlTemplate, accountNumber)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransfer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id", is(1)))
                .andExpect(jsonPath("$.Result.amount", is(100.0)))
                .andExpect(jsonPath("$.Result.accountTransactionType", is("TRANSFER")));

        verify(bankingAccountService, times(1)).createTransferTransaction(accountNumber, requestTransfer);
    }

    @Test @DisplayName("Given account number and transaction ID, when getTransactionByAccountNumber is called, then return transaction")
    void givenAccountNumberAndTransactionId_whenGetTransactionByAccountNumber_thenReturnAccountTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/{transactionId}";
        String accountNumber = "123456789";
        Long transactionId = 1L;
        AccountTransaction transaction = AccountTransaction.builder()
                .id(transactionId)
                .amount(100.0)
                .accountTransactionType(AccountTransactionType.RECHARGE)
                .build();
        when(bankingAccountService.getTransactionByAccountNumber(accountNumber, transactionId))
                .thenReturn(transaction);

        mockMvc.perform(get(urlTemplate, accountNumber, transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id", is(1)))
                .andExpect(jsonPath("$.Result.amount", is(100.0)))
                .andExpect(jsonPath("$.Result.accountTransactionType", is("RECHARGE")));

        verify(bankingAccountService, times(1)).getTransactionByAccountNumber(accountNumber, transactionId);
    }

    @Test @DisplayName("Given account number, transaction ID, and request body, when updateTransaction is called, then return updated transaction")
    void givenAccountNumberAndRequestUpdateTransaction_whenUpdateTransaction_thenReturnUpdateTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/{transactionId}";
        String accountNumber = "123456789";
        Long transactionId = 1L;
        RequestUpdateTransaction updateTransactionRequest = RequestUpdateTransaction.builder()
                .amount(50.0)
                .accountTransactionType(AccountTransactionType.WITHDRAWAL)
                .build();

        AccountTransaction updatedTransaction = AccountTransaction.builder()
                .id(transactionId)
                .amount(50.0)
                .accountTransactionType(AccountTransactionType.WITHDRAWAL)
                .build();
        when(bankingAccountService.updateTransaction(accountNumber, transactionId, updateTransactionRequest))
                .thenReturn(updatedTransaction);

        mockMvc.perform(put(urlTemplate, accountNumber, transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id", is(1)))
                .andExpect(jsonPath("$.Result.amount", is(50.0)))
                .andExpect(jsonPath("$.Result.accountTransactionType", is("WITHDRAWAL")));

        verify(bankingAccountService, times(1)).updateTransaction(accountNumber, transactionId, updateTransactionRequest);
    }

    @Test @DisplayName("Given account number and transaction ID, when deleteTransaction is called, then return transaction deleted response")
    void givenAccountNumberAndTransactionId_whenDeleteTransaction_thenDeleteTransaction() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/transaction/{transactionId}";
        String accountNumber = "123456789";
        Long transactionId = 1L;

        mockMvc.perform(delete(urlTemplate, accountNumber, transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result", is("Transaction deleted")));

        verify(bankingAccountService, times(1)).deleteTransaction(accountNumber, transactionId);
    }

    @Test @DisplayName("Given account number, when getAllTransactionsByAccount is called, then return a list of transactions")
    void givenAccountNumber_whenGetAllTransactionsByAccount_thenReturnAccountTransactionList() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions";
        String accountNumber = "123456789";
        List<AccountTransaction> transactions = Arrays.asList(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.TRANSFER).amount(100.0).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.WITHDRAWAL).amount(50.0).build()
        );

        when(bankingAccountService.getAllTransactionsByAccount(accountNumber)).thenReturn(transactions);

        mockMvc.perform(get(urlTemplate, accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total", is(2)))
                .andExpect(jsonPath("$.Result", hasSize(2)))
                .andExpect(jsonPath("$.Result[0].id", is(1)))
                .andExpect(jsonPath("$.Result[0].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[0].amount", is(100.0)))
                .andExpect(jsonPath("$.Result[1].id", is(2)))
                .andExpect(jsonPath("$.Result[1].accountTransactionType", is("WITHDRAWAL")))
                .andExpect(jsonPath("$.Result[1].amount", is(50.0)));

        verify(bankingAccountService, times(1)).getAllTransactionsByAccount(accountNumber);
    }

    @Test @DisplayName("Given account number and transaction type filter, when filterTransactionsByType is called, then return filtered transactions")
    void givenAccountNumberAndAccountTransactionType_whenFilterTransactionsByType_thenReturnAccountTransactionList() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/filterByType/{transactionTypeFilter}";
        String accountNumber = "123456789";
        AccountTransactionType transactionTypeFilter = AccountTransactionType.TRANSFER;
        List<AccountTransaction> filteredTransactions = Arrays.asList(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.TRANSFER).amount(100.0).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.TRANSFER).amount(50.0).build()
        );
        when(bankingAccountService.getAllTransactionsByType(accountNumber, transactionTypeFilter)).thenReturn(filteredTransactions);

        mockMvc.perform(get(urlTemplate, accountNumber, transactionTypeFilter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total", is(2)))
                .andExpect(jsonPath("$.Result", hasSize(2)))
                .andExpect(jsonPath("$.Result[0].id", is(1)))
                .andExpect(jsonPath("$.Result[0].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[0].amount", is(100.0)))
                .andExpect(jsonPath("$.Result[1].id", is(2)))
                .andExpect(jsonPath("$.Result[1].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[1].amount", is(50.0)));

        verify(bankingAccountService, times(1)).getAllTransactionsByType(accountNumber, transactionTypeFilter);
    }

    @Test @DisplayName("Given account number, start date, and end date, when filterTransactionsByDateRange is called, then return filtered transactions")
    void givenAccountNumberAndDateRange_whenFilterTransactionsByDateRange_thenReturnAccountTransactionList() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/filterByDateRange";
        String accountNumber = "123456789";
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        List<AccountTransaction> filteredTransactions = Arrays.asList(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.TRANSFER).amount(100.0).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.WITHDRAWAL).amount(50.0).build()
        );

        when(bankingAccountService.getAllTransactionsByDateRange(accountNumber, fromDate, toDate)).thenReturn(filteredTransactions);

        mockMvc.perform(get(urlTemplate, accountNumber)
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total", is(2)))
                .andExpect(jsonPath("$.Result", hasSize(2)))
                .andExpect(jsonPath("$.Result[0].id", is(1)))
                .andExpect(jsonPath("$.Result[0].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[0].amount", is(100.0)))
                .andExpect(jsonPath("$.Result[1].id", is(2)))
                .andExpect(jsonPath("$.Result[1].accountTransactionType", is("WITHDRAWAL")))
                .andExpect(jsonPath("$.Result[1].amount", is(50.0)));

        verify(bankingAccountService, times(1)).getAllTransactionsByDateRange(accountNumber, fromDate, toDate);
    }

    @Test @DisplayName("Given account number, transaction type filter, start date, and end date, when filterTransactionsByTypeAndDateRange is called, then return filtered transactions")
    void givenAccountNumberAndAccountTransactionTypeAndDateRange_whenFilterTransactionsByTypeAndDateRange_thenReturnAccountTransactionList() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/transactions/filterByTypeAndDateRange";
        String accountNumber = "123456789";
        AccountTransactionType transactionTypeFilter = AccountTransactionType.TRANSFER;
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 12, 31);
        List<AccountTransaction> filteredTransactions = Arrays.asList(
                AccountTransaction.builder().id(1L).accountTransactionType(AccountTransactionType.TRANSFER).amount(100.0).build(),
                AccountTransaction.builder().id(2L).accountTransactionType(AccountTransactionType.TRANSFER).amount(50.0).build()
        );
        when(bankingAccountService.getAllTransactionsByTypeAndDateRange(accountNumber, transactionTypeFilter, fromDate, toDate))
                .thenReturn(filteredTransactions);

        mockMvc.perform(get(urlTemplate, accountNumber)
                        .param("transactionTypeFilter", transactionTypeFilter.toString())
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total", is(2)))
                .andExpect(jsonPath("$.Result", hasSize(2)))
                .andExpect(jsonPath("$.Result[0].id", is(1)))
                .andExpect(jsonPath("$.Result[0].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[0].amount", is(100.0)))
                .andExpect(jsonPath("$.Result[1].id", is(2)))
                .andExpect(jsonPath("$.Result[1].accountTransactionType", is("TRANSFER")))
                .andExpect(jsonPath("$.Result[1].amount", is(50.0)));

        verify(bankingAccountService, times(1))
                .getAllTransactionsByTypeAndDateRange(accountNumber, transactionTypeFilter, fromDate, toDate);
    }
}