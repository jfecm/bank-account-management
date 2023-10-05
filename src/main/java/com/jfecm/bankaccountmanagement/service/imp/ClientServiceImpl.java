package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.exceptions.*;
import com.jfecm.bankaccountmanagement.repository.BankingAccountRepository;
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
    private final BankingAccountRepository bankingAccountRepository;

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
                .withdrawalLimit(5000.0)
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

            checkClientStatus(existingClient);

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
    public void updateClientStatusByDni(String dni, UserStatus status) {
        try {
            Client client = getClientByDni(dni);

            if (!status.equals(client.getUserStatus())) {
                client.setUserStatus(status);
                clientRepository.save(client);
                log.info("updateClientStatus() - OK.");
            }

        } catch (ResourceNotFoundException e) {
            log.error("updateClientStatus() - Client not found with DNI: {}", dni);
            throw e;
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
        Client client = getClientByDni(dni);
        client.setUserStatus(UserStatus.INACTIVE);
        clientRepository.save(client);
        log.info("deleteClientByDni() - OK.");
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
    public List<ResponseClientData> getAllClients(UserStatus status) {
        List<ResponseClientData> clients = new ArrayList<>();

        for (Client client : clientRepository.findByUserStatus(status)) {
            ResponseClientData c = mapper.map(client, ResponseClientData.class);
            clients.add(c);
        }

        log.info("Returning the list of clients. List size: " + clients.size());

        return clients;
    }

    /**
     * Validates if a unique identification number (DNI) already exists in the database.
     *
     * @param dni The identification number to validate.
     * @throws DniAlreadyExistsException If the DNI is already registered in the database.
     */
    private void validateIfDniExists(String dni) {
        if (clientRepository.existsByDni(dni)) {
            log.error("The DNI={} is already registered.", dni);
            throw new DniAlreadyExistsException("The DNI is already registered.");
        }
    }

    /**
     * Checks if a client has an active status.
     *
     * @param client The client to verify.
     * @throws InactiveAccountException If the client is not active.
     */
    @Override
    public void checkClientStatus(Client client) {
        if (client.getUserStatus() != UserStatus.ACTIVE) {
            throw new InactiveAccountException("The client is not active.");
        }
    }

    /**
     * Adds an adherent client to a main client.
     *
     * @param dni               The DNI of the main client.
     * @param adherentRequest   The data of the adherent client to add.
     * @return The added adherent client.
     */
    @Override
    public Client addClientAdherent(String dni, RequestCreateClient adherentRequest) {
        Client mainClient = getClientByDni(dni);

        validateIfDniExists(adherentRequest.getDni());

        adherentRequest.setUserStatus(UserStatus.ACTIVE);

        Client adherent = new Client();
        mapper.map(adherentRequest, adherent);

        // Create a default BankingAccount
        BankingAccount defaultAccount = createDefaultBankingAccount(adherent);
        adherent.setBankingAccount(defaultAccount);

        adherent.setMainClient(mainClient);

        return clientRepository.save(adherent);
    }

    /**
     * Gets the list of adherent clients of a main client.
     *
     * @param dni The DNI of the main client.
     * @return The list of adherent clients.
     */
    @Override
    public List<Client> getClientAdherentsList(String dni) {
        Client client = getClientByDni(dni);
        return client.getAdherents();
    }

    /**
     * Gets the details of an adherent client of a main client.
     *
     * @param dniMain     The DNI of the main client.
     * @param dniAdherent The DNI of the adherent client.
     * @return The details of the adherent client.
     * @throws ResourceNotFoundException If the adherent client is not associated with the main client.
     */
    @Override
    public Client getClientAdherentDetails(String dniMain, String dniAdherent) {
        Client client = getClientByDni(dniMain);
        Client clientAdherent = getClientByDni(dniAdherent);

        for (Client adherent : client.getAdherents()) {
            if (adherent.getDni().equals(clientAdherent.getDni())) {
                return adherent;
            }
        }

        throw new ResourceNotFoundException("The client with DNI " + dniAdherent + " is not a adherent of " + dniMain);
    }

    /**
     * Removes an adherent client from a main client and unlinks their banking account.
     *
     * @param dniMain     The DNI of the main client.
     * @param dniAdherent The DNI of the adherent client to remove.
     * @throws ResourceNotFoundException If the adherent client is not associated with the main client.
     */
    @Override
    public void removeClientAdherent(String dniMain, String dniAdherent) {
        Client client = getClientByDni(dniMain);
        Client clientAdherent = getClientByDni(dniAdherent);

        for (Client adherent : client.getAdherents()) {
            if (adherent.getDni().equals(clientAdherent.getDni())) {
                BankingAccount bankingAccount = adherent.getBankingAccount();
                bankingAccount.setClient(null);
                clientRepository.deleteById(clientAdherent.getId());
                bankingAccountRepository.deleteById(bankingAccount.getId());
                return;
            }
        }

        throw new ResourceNotFoundException("The client with DNI " + dniAdherent + " is not a adherent of " + dniMain);

    }

    /**
     * Updates the details of an adherent client of a main client.
     *
     * @param dniMain           The DNI of the main client.
     * @param dniAdherent       The DNI of the adherent client to update.
     * @param adherentRequest   The new data for the adherent client.
     * @return The updated adherent client.
     * @throws ResourceNotFoundException If the adherent client is not associated with the main client.
     */
    @Override
    public Client updateClientAdherentDetails(String dniMain, String dniAdherent, RequestUpdateClient adherentRequest) {
        Client client = getClientByDni(dniMain);
        Client clientAdherent = getClientByDni(dniAdherent);

        for (Client adherent : client.getAdherents()) {
            if (adherent.getDni().equals(clientAdherent.getDni())) {
                mapper.map(adherentRequest, adherent);
                return clientRepository.save(adherent);
            }
        }

        throw new ResourceNotFoundException("The client with DNI " + dniAdherent + " is not a adherent of " + dniMain);
    }

    /**
     * Changes the status of an adherent client of a main client.
     *
     * @param dniMain       The DNI of the main client.
     * @param dniAdherent   The DNI of the adherent client whose status will be changed.
     * @param status        The new status for the adherent client.
     * @throws ResourceNotFoundException If the adherent client is not associated with the main client.
     */
    @Override
    public void changeClientAdherentStatus(String dniMain, String dniAdherent, UserStatus status) {
        Client client = getClientByDni(dniMain);
        Client clientAdherent = getClientByDni(dniAdherent);

        for (Client adherent : client.getAdherents()) {
            if (adherent.getDni().equals(clientAdherent.getDni())) {
                adherent.setUserStatus(status);
                clientRepository.save(adherent);
                return;
            }
        }

        throw new ResourceNotFoundException("The client with DNI " + dniAdherent + " is not a adherent of " + dniMain);
    }
}
