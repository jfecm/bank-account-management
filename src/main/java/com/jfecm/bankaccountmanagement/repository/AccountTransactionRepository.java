package com.jfecm.bankaccountmanagement.repository;

import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
}
