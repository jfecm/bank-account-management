package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("BankingAccountController Tests")
@WebMvcTest(BankingAccountController.class)
class BankingAccountControllerTest {
    @MockBean
    private BankingAccountService bankingAccountService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test @DisplayName("Given a account number, when getBankingAccount is called, then return the banking account details")
    void givenAccountNumber_whenGetBankingAccount_thenReturnBankingAccountDetails() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}";
        String accountNumber = "123456789";
        BankingAccount account = BankingAccount.builder().accountNumber(accountNumber).balance(1000.0).build();
        when(bankingAccountService.getBankingAccountByAccountNumber(accountNumber)).thenReturn(account);

        mockMvc.perform(get(urlTemplate, accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.accountNumber").value(accountNumber))
                .andExpect(jsonPath("$.Result.balance").value(1000.0));

        verify(bankingAccountService, times(1)).getBankingAccountByAccountNumber(accountNumber);
    }

    @Test @DisplayName("Given a valid status, when getAllBankingAccounts is called, then return a list of banking accounts")
    void givenBankingAccountStatus_whenGetAllBankingAccounts_thenReturnBankingAccountList() throws Exception {
        String urlTemplate = "/api/v1/accounts";
        BankingAccountStatus statusParam = BankingAccountStatus.ACTIVE;
        List<BankingAccount> accounts = List.of(
                BankingAccount.builder()
                        .accountNumber("123").balance(1000.0).bankingAccountStatus(BankingAccountStatus.ACTIVE)
                        .build(),
                BankingAccount.builder()
                        .accountNumber("456").balance(2000.0).bankingAccountStatus(BankingAccountStatus.ACTIVE)
                        .build()
        );
        when(bankingAccountService.getAllBankingAccounts(BankingAccountStatus.ACTIVE)).thenReturn(accounts);

        mockMvc.perform(get(urlTemplate).param("status", statusParam.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total").value(2))
                .andExpect(jsonPath("$.Result[0].accountNumber").value("123"))
                .andExpect(jsonPath("$.Result[1].accountNumber").value("456"))
                .andExpect(jsonPath("$.Result[0].balance").value(1000.0))
                .andExpect(jsonPath("$.Result[1].balance").value(2000.0));

        verify(bankingAccountService, times(1)).getAllBankingAccounts(BankingAccountStatus.ACTIVE);
    }


    @Test @DisplayName("Given an account number and a new account status, when updateBankingAccountStatus is called, then update the account status")
    void givenAccountNumberAndBankingAccountStatus_whenUpdateBankingAccountStatus_thenUpdateBankingAccountStatus() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{accountNumber}/status/{newAccountStatus}";
        String accountNumber = "123456789";
        BankingAccountStatus newAccountStatus = BankingAccountStatus.INACTIVE;
        doNothing().when(bankingAccountService).updateBankingAccountStatusByAccountNumber(accountNumber, newAccountStatus);

        mockMvc.perform(put(urlTemplate, accountNumber, newAccountStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Banking account status updated."));

        verify(bankingAccountService, times(1)).updateBankingAccountStatusByAccountNumber(accountNumber, newAccountStatus);
    }


    @Test @DisplayName("Given an account number, when deleteBankingAccount is called, then delete the banking account")
    void givenAccountNumber_whenDeleteBankingAccount_thenDeleteBankingAccount() throws Exception {
        String urlTemplate = "/api/v1/accounts/account/{number}";
        String accountNumber = "123456789";
        doNothing().when(bankingAccountService).deleteBankingAccount(accountNumber);

        mockMvc.perform(delete(urlTemplate, accountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Banking account deleted."));

        verify(bankingAccountService, times(1)).deleteBankingAccount(accountNumber);
    }

}