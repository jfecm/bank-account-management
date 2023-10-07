package com.jfecm.bankaccountmanagement.repository;

import com.jfecm.bankaccountmanagement.builders.BankingAccountBuilder;
import com.jfecm.bankaccountmanagement.builders.ClientBuilder;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BankingAccountRepository Tests")
class BankingAccountRepositoryTest {

    @Autowired
    private BankingAccountRepository bankingAccountRepository;
    @Autowired
    private ClientRepository clientRepository;
    private BankingAccount bankingAccount;
    private Client clientWithBankingAccount;

    @BeforeEach
    void setUp() {
        clientWithBankingAccount = ClientBuilder.buildClientWithBankingAccountRepository();
        clientRepository.save(clientWithBankingAccount);
        bankingAccount = clientWithBankingAccount.getBankingAccount();
    }

    @AfterEach
    void tearDown() {
        clientRepository.delete(clientWithBankingAccount);
    }

    @Test
    @DisplayName("Given account number, when finding by account number, then return BankingAccount")
    void givenAccountNumber_whenFindByAccountNumber_thenReturnBankingAccount() {
        BankingAccount foundBankingAccount = bankingAccountRepository.findByAccountNumber(bankingAccount.getAccountNumber());

        assertNotNull(foundBankingAccount);
        assertEquals(bankingAccount.getAccountNumber(), foundBankingAccount.getAccountNumber());
    }

    @Test
    @DisplayName("Given account number, when finding by account number, then return null")
    void givenAccountNumber_whenFindByAccountNumber_thenReturnNull() {
        BankingAccount foundBankingAccount = bankingAccountRepository.findByAccountNumber(BankingAccountBuilder.getRandomAccountNumber());

        assertNull(foundBankingAccount);
    }

    @Test
    @DisplayName("Given banking account status, when finding by status, then return BankingAccount list")
    void givenBankingAccountStatus_whenFindByBankingAccountStatus_thenReturnBankingAccountList() {
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findByBankingAccountStatus(BankingAccountStatus.ACTIVE);

        assertNotNull(bankingAccountList);
        assertEquals(1, bankingAccountList.size());

    }

    @Test
    @DisplayName("Given banking account status, when finding by status, then return empty list")
    void givenBankingAccountStatus_whenFindByBankingAccountStatus_thenReturnEmptyList() {
        List<BankingAccount> bankingAccountList = bankingAccountRepository.findByBankingAccountStatus(BankingAccountStatus.CLOSED);

        assertNotNull(bankingAccountList);
        assertEquals(0, bankingAccountList.size());
        assertTrue(bankingAccountList.isEmpty());
    }

}