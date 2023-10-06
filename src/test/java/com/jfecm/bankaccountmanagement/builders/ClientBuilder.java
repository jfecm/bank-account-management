package com.jfecm.bankaccountmanagement.builders;

import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;

import java.util.UUID;

public class ClientBuilder {

    public static Client buildClientWithoutBankingAccount() {
        Client client = new Client();
        client.setDni(getRandomDni());
        client.setName("name test");
        client.setAddress("address test");
        client.setEmail("email@gmail.com");
        client.setPassword("<PASSWORD>");
        client.setUserStatus(UserStatus.ACTIVE);
        client.setBankingAccount(null);
        client.setAdherents(null);
        client.setMainClient(null);
        return client;
    }

    public static Client buildClientWithBankingAccount() {
        Client client = new Client();
        client.setDni(getRandomDni());
        client.setName("name test");
        client.setAddress("address test");
        client.setEmail("email@gmail.com");
        client.setPassword("<PASSWORD>");
        client.setUserStatus(UserStatus.ACTIVE);
        client.setBankingAccount(BankingAccountBuilder.buildBankingAccount());
        client.setAdherents(null);
        client.setMainClient(null);
        return client;
    }

    public static String getRandomDni() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        // Get the UUID as a string and remove any dashes
        // Take the first 9 characters of the UUID string as the DNI
        return uuid.toString().replace("-", "").substring(0, 9);
    }
}
