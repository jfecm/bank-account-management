package com.jfecm.bankaccountmanagement.dto.response;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ResponseClientData {
    private Long id;
    private String dni;
    private String name;
    private String email;
    private String address;
    private UserStatus userStatus;
    private BankingAccount bankingAccount;
    private List<Client> adherents;
    private Client mainClient;
}
