package com.jfecm.bankaccountmanagement.builders;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientBuilder {

    public static Client buildClientWithoutBankingAccountRepository() {
        return Client.builder()
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.ACTIVE)
                .bankingAccount(null)
                .adherents(null)
                .mainClient(null)
                .build();
    }

    public static Client buildClientWithBankingAccountService() {
        return Client.builder()
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.ACTIVE)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .adherents(null)
                .mainClient(null)
                .build();
    }

    public static Client buildClientWithBankingAccountRepository() {
        return Client.builder()
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.ACTIVE)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountRepository())
                .adherents(null)
                .mainClient(null)
                .build();
    }

    public static String getRandomDni() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        // Get the UUID as a string and remove any dashes
        // Take the first 9 characters of the UUID string as the DNI
        return uuid.toString().replace("-", "").substring(0, 9);
    }

    public static RequestCreateClient buildRequestCreateClientService() {
        return RequestCreateClient.builder()
                .dni(getRandomDni())
                .name("name test create")
                .email("email_create@gmail.com")
                .password("<PASSWORD_create>")
                .address("address test create")
                .build();
    }

    public static Client buildClientWithIdService() {
        return Client.builder()
                .id(1L)
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.PENDING)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .adherents(null)
                .mainClient(null)
                .build();
    }

    public static RequestUpdateClient buildRequestUpdateClientService() {
        return RequestUpdateClient.builder()
               .name("name test update")
               .address("address test update")
               .build();
    }

    public static Client buildClientActiveService() {
        return Client.builder()
                .id(1L)
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.ACTIVE)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .adherents(null)
                .mainClient(null)
                .build();
    }

    public static Client buildClientWithAdherentsService() {
        Client adherent1 = Client.builder()
                .id(2L)
                .dni(getRandomDni())
                .email("email1adherent")
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .build();
        Client adherent2 = Client.builder()
                .id(3L)
                .dni(getRandomDni())
                .email("email2adherent")
                .build();
        List<Client> adherents = new ArrayList<>();
        adherents.add(adherent1);
        adherents.add(adherent2);

        return Client.builder()
                .id(1L)
                .dni(getRandomDni())
                .name("name test")
                .address("address test")
                .email("email@gmail.com")
                .password("<PASSWORD>")
                .userStatus(UserStatus.ACTIVE)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .adherents(adherents)
                .mainClient(null)
                .build();
    }
}
