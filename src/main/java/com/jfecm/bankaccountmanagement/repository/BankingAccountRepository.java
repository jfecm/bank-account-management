package com.jfecm.bankaccountmanagement.repository;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankingAccountRepository extends JpaRepository<BankingAccount, Long>{
    BankingAccount findByAccountNumber(String accountNumber);
    List<BankingAccount> findByBankingAccountStatus(BankingAccountStatus bankingAccountStatus);
}
