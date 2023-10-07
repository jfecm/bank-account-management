package com.jfecm.bankaccountmanagement.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestUpdateClient {
    private String name;
    private String address;
}
