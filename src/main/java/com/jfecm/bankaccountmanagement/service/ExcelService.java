package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.entity.Client;

// Using 'Apache POI library ' to create, read, write, and modify Excel files.
public interface ExcelService {
    byte[] generateAccountTransactionsByDateRangeExcel(Client client);
}
