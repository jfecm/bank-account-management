package com.jfecm.bankaccountmanagement.dto.request;

import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import lombok.Data;

@Data
public class RequestUpdateTransaction {
    private AccountTransactionType accountTransactionType;
    private Double amount;
}
