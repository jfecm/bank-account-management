package com.jfecm.bankaccountmanagement.repository;

import com.jfecm.bankaccountmanagement.builders.ClientBuilder;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ClientRepository Tests")
class ClientRepositoryTest {

    @Autowired
    private ClientRepository clientRepository;
    private Client client;

    @BeforeEach
    void setUp() {
        client = ClientBuilder.buildClientWithoutBankingAccountRepository();
    }

    @AfterEach
    void tearDown() {
        client = null;
    }

    @Test
    @DisplayName("Given a unique DNI, when searching for an existing DNI, then return false")
    void givenDni_whenSearchExistsByDni_thenReturnFalse() {
        clientRepository.save(client);
        boolean result = clientRepository.existsByDni(ClientBuilder.getRandomDni());
        assertFalse(result);
    }

    @Test
    @DisplayName("Given a unique DNI, when searching for an existing DNI, then return true")
    void givenDni_whenSearchExistsByDni_thenReturnTrue() {
        clientRepository.save(client);
        boolean result = clientRepository.existsByDni(client.getDni());
        assertTrue(result);
    }

    @Test
    @DisplayName("Given a client, when searching by DNI, then return the client")
    void givenClient_whenFindByDni_thenReturnClient() {
        // Save a client with a unique DNI
        clientRepository.save(client);

        // Search for the client by DNI
        Client foundClient = clientRepository.findByDni(client.getDni());

        // Assert that the found client is not null and has the expected DNI
        assertNotNull(foundClient);
        assertEquals(client.getDni(), foundClient.getDni());
        assertEquals(client.getName(), foundClient.getName());
        assertEquals(client.getEmail(), foundClient.getEmail());
    }

    @Test
    @DisplayName("Given a client, when searching by non-existent DNI, then return null")
    void givenClient_whenFindByDni_thenReturnNull() {
        // Save a client with a unique DNI
        clientRepository.save(client);

        // Search for the client by DNI
        Client foundClient = clientRepository.findByDni(ClientBuilder.getRandomDni());

        // Assert that the found client is null
        assertNull(foundClient);
    }

    @Test
    @DisplayName("Given a UserStatus, when searching by UserStatus, then return a list of clients")
    void givenUserStatus_whenFindByUserStatus_thenReturnClientList() {
        // Save a client with a specific UserStatus
        clientRepository.save(client);

        // Search for clients by UserStatus
        List<Client> activeClients = clientRepository.findByUserStatus(UserStatus.ACTIVE);

        // Assert that the list contains the saved client with the expected UserStatus
        assertNotNull(activeClients);
        assertEquals(1, activeClients.size());
        assertEquals(UserStatus.ACTIVE, activeClients.get(0).getUserStatus());
        assertEquals(client.getDni(), activeClients.get(0).getDni());
    }

    @Test
    @DisplayName("Given a UserStatus, when searching by non-existent UserStatus, then return an empty list")
    void givenUserStatus_whenFindByUserStatus_thenReturnNull() {
        // Save a client with a specific UserStatus
        clientRepository.save(client);

        // Search for clients by UserStatus
        List<Client> clientList = clientRepository.findByUserStatus(UserStatus.INACTIVE);

        // Assert that the list contains the saved client with the expected UserStatus
        assertTrue(clientList.isEmpty());
    }
}