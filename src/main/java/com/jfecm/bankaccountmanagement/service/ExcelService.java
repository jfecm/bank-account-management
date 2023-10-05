package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.entity.AccountTransaction;

import java.util.List;

// Using 'Apache POI library ' to create, read, write, and modify Excel files.
public interface ExcelService {
    byte[] generateAccountTransactionsByDateRangeExcel(List<AccountTransaction> transactions);
}
