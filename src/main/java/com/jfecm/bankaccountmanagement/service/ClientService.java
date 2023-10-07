package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;

import java.util.List;

public interface ClientService {
    Client saveClient(RequestCreateClient client);

    Client updateClientByDni(String dni, RequestUpdateClient client);

    void updateClientStatusByDni(String dni, UserStatus status);

    void deleteClientByDni(String dni);

    Client getClientByDni(String dni);

    List<ResponseClientData> getAllClients(UserStatus status);

    void checkClientStatus(Client client);

    Client addClientAdherent(String dni, RequestCreateClient adherentRequest);

    List<Client> getClientAdherentsList(String dni);

    Client getClientAdherentDetails(String dniMain, String dniAdherent);

    void removeClientAdherent(String dniMain, String dniAdherent);

    Client updateClientAdherentDetails(String dniMain, String dniAdherent, RequestUpdateClient adherentRequest);

    void changeClientAdherentStatus(String dniMain, String dniAdherent, UserStatus status);
}
