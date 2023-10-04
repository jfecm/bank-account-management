package com.jfecm.bankaccountmanagement.dto.request;

import lombok.Data;

@Data
public class RequestCreateTransaction {
    private Double amount;
    private String destinationAccountNumber;
}
