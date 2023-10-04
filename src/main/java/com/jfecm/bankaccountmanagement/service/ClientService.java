package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;

import java.util.List;

public interface ClientService {
    void saveClient(RequestCreateClient client);

    Client updateClientByDni(String dni, RequestUpdateClient client);

    void updateClientStatusByDni(String dni, String newUserStatus);

    void deleteClientByDni(String dni);

    Client getClientByDni(String dni);

    List<ResponseClientData> getAllClients(String status);
}
