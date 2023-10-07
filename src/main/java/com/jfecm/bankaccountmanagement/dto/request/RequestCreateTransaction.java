package com.jfecm.bankaccountmanagement.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestCreateTransaction {
    private Double amount;
    private String destinationAccountNumber;
}
