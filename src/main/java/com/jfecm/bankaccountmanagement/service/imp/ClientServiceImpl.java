package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.exceptions.DniAlreadyExistsException;
import com.jfecm.bankaccountmanagement.exceptions.EmailDuplicateException;
import com.jfecm.bankaccountmanagement.exceptions.InvalidStatusException;
import com.jfecm.bankaccountmanagement.exceptions.ResourceNotFoundException;
import com.jfecm.bankaccountmanagement.repository.ClientRepository;
import com.jfecm.bankaccountmanagement.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {
    private final ModelMapper mapper;
    private final ClientRepository clientRepository;

    /**
     * Register a new client.
     *
     * @param client The client data to register.
     * @throws DniAlreadyExistsException If the DNI is already registered.
     * @throws EmailDuplicateException   If the email is already registered.
     */
    @Override
    public void saveClient(RequestCreateClient client) {
        try {
            validateIfDniExists(client.getDni());

            client.setUserStatus(UserStatus.PENDING);

            Client clientEntity = new Client();
            mapper.map(client, clientEntity);

            // Create a default BankingAccount
            BankingAccount defaultAccount = createDefaultBankingAccount(clientEntity);
            clientEntity.setBankingAccount(defaultAccount);

            clientRepository.save(clientEntity);

            log.info("saveClient() - OK.");
        } catch (DniAlreadyExistsException e) {
            log.error("saveClient() - Error= {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException ex) {
            throw new EmailDuplicateException("Email already exists.");
        }
    }

    /**
     * Generate a default Banking Account
     *
     * @param client client data for the account
     * @return banking account
     */
    private BankingAccount createDefaultBankingAccount(Client client) {
        return BankingAccount.builder()
                .client(client)
                .accountNumber(generateUniqueAccountNumber())
                .balance(0.0)
                .overdraftLimit(5000.0)
                .accountOpenedDate(LocalDate.now())
                .bankingAccountStatus(BankingAccountStatus.ACTIVE)
                .build();
    }

    /**
     * Generate a random Unique Account Number
     *
     * @return Unique account number with length 10
     */
    private String generateUniqueAccountNumber() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        // Convert the UUID to a string and remove the dashes
        // Limit the length of the account number to, for example, 10 characters
        return uuid.toString().replace("-", "");
    }


    /**
     * Update a client's data by their DNI.
     *
     * @param dni          The DNI of the client to update.
     * @param updateClient The new client data.
     * @return The updated client.
     * @throws ResourceNotFoundException If the client is not found.
     */
    @Override
    public Client updateClientByDni(String dni, RequestUpdateClient updateClient) {
        try {
            Client existingClient = getClientByDni(dni);

            mapper.map(updateClient, existingClient);

            Client client = clientRepository.save(existingClient);
            log.info("updateClient() - OK.");
            return client;
        } catch (ResourceNotFoundException e) {
            log.error("updateClient() - Client not found with DNI: {}", dni);
            throw e;
        }
    }

    /**
     * Update the status of a client by their DNI.
     *
     * @param dni    The DNI of the client to update.
     * @param status The new state of the client.
     * @throws ResourceNotFoundException If the client is not found.
     * @throws InvalidStatusException    If an invalid status is provided.
     */
    @Override
    public void updateClientStatusByDni(String dni, String status) {
        try {
            Client existingClient = getClientByDni(dni);

            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());

            if (!userStatus.equals(existingClient.getUserStatus())) {
                existingClient.setUserStatus(userStatus);
                clientRepository.save(existingClient);
                log.info("updateClientStatus() - OK.");
            }

        } catch (ResourceNotFoundException e) {
            log.error("updateClientStatus() - Client not found with DNI: {}", dni);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Invalid status provided: " + status);
            throw new InvalidStatusException("Invalid status provided.");
        }
    }

    /**
     * Change the status of a client to inactive by their DNI.
     *
     * @param dni The DNI of the client to delete.
     * @throws ResourceNotFoundException If the client is not found.
     */
    @Override
    public void deleteClientByDni(String dni) {
        try {
            Client existingClient = getClientByDni(dni);
            existingClient.setUserStatus(UserStatus.INACTIVE);
            clientRepository.save(existingClient);
        } catch (ResourceNotFoundException e) {
            log.error("updateClientStatus() - Client not found with DNI: {}", dni);
            throw e;
        }
    }

    /**
     * Obtains a client by their DNI.
     *
     * @param dni The DNI of the client to obtain.
     * @return The client found.
     * @throws ResourceNotFoundException If the client is not found.
     */
    @Override
    public Client getClientByDni(String dni) {
        Client client = clientRepository.findByDni(dni);
        if (client == null) {
            throw new ResourceNotFoundException("Client not found with DNI: " + dni);
        }
        return client;
    }

    /**
     * Gets a list of customers with a specific status.
     *
     * @param status The status of the clients to obtain.
     * @return List of clients with the specified status.
     * @throws InvalidStatusException If an invalid status is provided.
     */
    @Override
    public List<ResponseClientData> getAllClients(String status) {
        List<ResponseClientData> clients = new ArrayList<>();

        try {
            UserStatus userStatus = UserStatus.valueOf(status.toUpperCase());

            for (Client client : clientRepository.findByUserStatus(userStatus)) {
                ResponseClientData c = mapper.map(client, ResponseClientData.class);
                clients.add(c);
            }

            log.info("Returning the list of clients. List size: " + clients.size());
        } catch (IllegalArgumentException e) {
            log.error("Invalid status provided: " + status);
            throw new InvalidStatusException("Invalid status provided.");
        }

        return clients;
    }

    private void validateIfDniExists(String dni) {
        if (clientRepository.existsByDni(dni)) {
            log.error("The DNI={} is already registered.", dni);
            throw new DniAlreadyExistsException("The DNI is already registered.");
        }
    }

}
