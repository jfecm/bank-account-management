package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.builders.BankingAccountBuilder;
import com.jfecm.bankaccountmanagement.builders.ClientBuilder;
import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.exceptions.DniAlreadyExistsException;
import com.jfecm.bankaccountmanagement.exceptions.EmailDuplicateException;
import com.jfecm.bankaccountmanagement.exceptions.InactiveAccountException;
import com.jfecm.bankaccountmanagement.exceptions.ResourceNotFoundException;
import com.jfecm.bankaccountmanagement.repository.BankingAccountRepository;
import com.jfecm.bankaccountmanagement.repository.ClientRepository;
import com.jfecm.bankaccountmanagement.service.imp.ClientServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@DisplayName("ClientServiceImpl Tests")
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {
    @InjectMocks
    private ClientServiceImpl clientService;
    @Mock
    private ModelMapper mapper;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private BankingAccountRepository bankingAccountRepository;
    private RequestCreateClient requestCreateClient;
    private RequestUpdateClient requestUpdateClient;
    private Client client;
    private Client clientActive;


    @BeforeEach
    void setUp() {
        client = ClientBuilder.buildClientWithIdService();
        clientActive = ClientBuilder.buildClientActiveService();
        requestCreateClient = ClientBuilder.buildRequestCreateClientService();
        requestUpdateClient = ClientBuilder.buildRequestUpdateClientService();
    }

    @AfterEach
    void tearDown() {
    }

    @Test @DisplayName("Given RequestCreateClient, When SaveClient, Then Save Client Successfully")
    void givenRequestCreateClient_whenSaveClient_thenSaveClient() {
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        Client clientCreated = clientService.saveClient(requestCreateClient);

        verify(clientRepository, times(1)).save(any(Client.class));
        assertNotNull(clientCreated);
        assertEquals(1L, clientCreated.getId());
        assertEquals(UserStatus.PENDING, clientCreated.getUserStatus());
        assertNotNull(clientCreated.getBankingAccount());
    }

    @Test @DisplayName("Given RequestCreateClient with Existing DNI, When SaveClient, Then Throw DniAlreadyExistsException")
    void givenRequestCreateClient_whenSaveClient_thenReturnDniAlreadyExistsException() {
        when(clientRepository.existsByDni(any(String.class))).thenReturn(true);

        DniAlreadyExistsException exception = assertThrows(DniAlreadyExistsException.class, () -> clientService.saveClient(requestCreateClient));

        verify(clientRepository, times(1)).existsByDni(requestCreateClient.getDni());
        assertEquals("The DNI is already registered.", exception.getMessage());
    }

    @Test @DisplayName("Given RequestCreateClient with Duplicate Email, When SaveClient, Then Throw EmailDuplicateException")
    void givenRequestCreateClient_whenSaveClient_thenReturnEmailDuplicateException() {
        when(clientRepository.save(any(Client.class))).thenThrow(new DataIntegrityViolationException("Email already exists."));

        EmailDuplicateException exception = assertThrows(EmailDuplicateException.class, () -> clientService.saveClient(requestCreateClient));

        verify(clientRepository, times(1)).save(any(Client.class));
        assertEquals("Email already exists.", exception.getMessage());
    }

    @Test @DisplayName("Given DNI and update request, when updating a client, then return the updated client")
    void givenDniAndRequestUpdateClient_whenUpdateClientByDni_thenReturnClient() {
        String dni = ClientBuilder.getRandomDni();
        Client updatedClient = Client.builder().name("name test update").address("address test update").build();
        when(clientRepository.findByDni(dni)).thenReturn(clientActive);
        when(clientRepository.save(clientActive)).thenReturn(updatedClient);

        Client result = clientService.updateClientByDni(dni, requestUpdateClient);

        assertNotNull(result);
        assertEquals("name test update", result.getName());
        assertEquals("address test update", result.getAddress());
        verify(clientRepository, times(1)).findByDni(dni);
        verify(clientRepository, times(1)).save(clientActive);
    }

    @Test @DisplayName("Given DNI and update request, when updating a client, then throw ResourceNotFoundException")
    void givenDniAndRequestUpdateClient_whenUpdateClientByDni_thenReturnResourceNotFoundException() {
        String dni = ClientBuilder.getRandomDni();
        when(clientRepository.findByDni(dni)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clientService.updateClientByDni(dni, requestUpdateClient));

        assertEquals("Client not found with DNI: " + dni, exception.getMessage());
        verify(clientRepository, times(1)).findByDni(dni);

    }

    @Test @DisplayName("Given inactive client and update request, when updating a client, then throw InactiveAccountException")
    void givenDniAndRequestUpdateClient_whenUpdateClientByDni_thenReturnInactiveAccountException() {
        String dni = ClientBuilder.getRandomDni();
        when(clientRepository.findByDni(dni)).thenReturn(client);

        InactiveAccountException exception = assertThrows(InactiveAccountException.class, () -> clientService.updateClientByDni(dni, requestUpdateClient));

        assertEquals("The client is not active.", exception.getMessage());
        verify(clientRepository, times(1)).findByDni(dni);
    }

    @Test @DisplayName("Given DNI and UserStatus, when updating client status, then the client status is updated")
    void givenValidDniAndUserStatus_whenUpdateClientStatusByDni_thenUpdateClientStatus() {
        String dni = ClientBuilder.getRandomDni();
        UserStatus newStatus = UserStatus.BANNED;
        when(clientRepository.findByDni(dni)).thenReturn(client);

        clientService.updateClientStatusByDni(dni, newStatus);

        assertEquals(newStatus, client.getUserStatus());
        verify(clientRepository, times(1)).findByDni(dni);
        verify(clientRepository, times(1)).save(client);
    }

    @Test @DisplayName("Given valid DNI, when deleting client by DNI, then set user status to INACTIVE")
    void givenValidDni_whenDeleteClientByDni_thenSetUserStatusToInactive() {
        String dni = ClientBuilder.getRandomDni();
        when(clientRepository.findByDni(dni)).thenReturn(client);

        clientService.deleteClientByDni(dni);

        assertEquals(UserStatus.INACTIVE, client.getUserStatus());
        verify(clientRepository, times(1)).findByDni(dni);
        verify(clientRepository, times(1)).save(client);
    }

    @Test @DisplayName("Given valid DNI, when getting client by DNI, then return the client")
    void givenDni_whenGetClientByDni_thenReturnClient() {
        String dni = ClientBuilder.getRandomDni();
        when(clientRepository.findByDni(dni)).thenReturn(client);

        Client result = clientService.getClientByDni(dni);

        assertNotNull(result);
        verify(clientRepository, times(1)).findByDni(dni);
    }

    @Test @DisplayName("Given invalid DNI, when getting client by DNI, then throw ResourceNotFoundException")
    void givenDni_whenGetClientByDni_thenReturnResourceNotFoundException() {
        String dni = ClientBuilder.getRandomDni();
        when(clientRepository.findByDni(dni)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> clientService.getClientByDni(dni));

        assertEquals("Client not found with DNI: " + dni, exception.getMessage());
        verify(clientRepository, times(1)).findByDni(dni);

    }

    @Test @DisplayName("Given active status, when getting all clients, then return a list of active clients")
    void givenActiveStatus_whenGetAllClients_thenReturnListOfActiveClients() {
        UserStatus activeStatus = UserStatus.ACTIVE;
        Client client1 = ClientBuilder.buildClientWithBankingAccountService();
        Client client2 = ClientBuilder.buildClientWithBankingAccountService();
        List<Client> mockClients = new ArrayList<>();
        mockClients.add(client1);
        mockClients.add(client2);

        when(clientRepository.findByUserStatus(activeStatus)).thenReturn(mockClients);

        List<ResponseClientData> result = clientService.getAllClients(activeStatus);

        assertEquals(mockClients.size(), result.size());
    }

    @Test @DisplayName("Given an inactive client, when checking client status, then throw InactiveAccountException")
    void givenInactiveClient_whenCheckClientStatus_thenThrowInactiveAccountException() {
        Client inactiveClient = ClientBuilder.buildClientWithIdService();
        assertThrows(InactiveAccountException.class, () -> clientService.checkClientStatus(inactiveClient));
    }

    @Test @DisplayName("Given valid DNI and RequestCreateClient, when adding client adherent, then return the saved adherent client")
    void givenValidDniAndRequestCreateClient_whenAddClientAdherent_thenReturnSavedClient() {
        Client client = ClientBuilder.buildClientWithIdService();
        client.setAdherents(new ArrayList<>());

        Client adherent = Client.builder()
                .dni(requestCreateClient.getDni())
                .name("name test create")
                .email("email_create@gmail.com")
                .password("<PASSWORD_create>")
                .address("address test create")
                .userStatus(UserStatus.ACTIVE)
                .mainClient(client)
                .bankingAccount(BankingAccountBuilder.buildBankingAccountService())
                .build();


        when(clientRepository.findByDni(client.getDni())).thenReturn(client);
        when(clientRepository.existsByDni(requestCreateClient.getDni())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(adherent);

        Client result = clientService.addClientAdherent(client.getDni(), requestCreateClient);

        verify(clientRepository, times(1)).findByDni(anyString());
        verify(clientRepository, times(1)).existsByDni(anyString());
        verify(clientRepository, times(1)).save(any(Client.class));

        assertNotNull(result);
        assertEquals(client.getDni(), result.getMainClient().getDni());
    }

    @Test @DisplayName("Given DniMain, when getting client adherents list, then return adherents list")
    void givenDniMain_whenGetClientAdherentsList_thenReturnAdherentsList() {
        Client clientWithAdherents = ClientBuilder.buildClientWithAdherentsService();
        when(clientRepository.findByDni(clientWithAdherents.getDni())).thenReturn(clientWithAdherents);

        List<Client> adherents = clientService.getClientAdherentsList(clientWithAdherents.getDni());

        verify(clientRepository, times(1)).findByDni(clientWithAdherents.getDni());
        assertEquals(2, adherents.size());
    }

    @Test @DisplayName("Given DniMain and DniAdherent, when getting client adherent details, then return adherent details")
    void givenDniMainAndDniAdherent_whenGetClientAdherentDetails_thenReturnAdherentDetails() {
        Client clientMain = ClientBuilder.buildClientWithAdherentsService();
        Client clientAdherent = clientMain.getAdherents().get(0);
        when(clientRepository.findByDni(clientMain.getDni())).thenReturn(clientMain);
        when(clientRepository.findByDni(clientAdherent.getDni())).thenReturn(clientAdherent);

        Client result = clientService.getClientAdherentDetails(clientMain.getDni(), clientAdherent.getDni());

        verify(clientRepository, times(2)).findByDni(anyString());
        assertEquals("email1adherent", result.getEmail());
        assertNotEquals(clientMain.getId(), result.getId());
    }

    @Test @DisplayName("Given DniMain and DniAdherent, when getting client adherent details, then throw ResourceNotFoundException")
    void givenDniMainAndDniAdherent_whenGetClientAdherentDetails_thenReturnResourceNotFoundException() {
        Client clientMain = ClientBuilder.buildClientWithAdherentsService();
        Client clientAdherent = ClientBuilder.buildClientWithIdService();
        when(clientRepository.findByDni(clientMain.getDni())).thenReturn(clientMain);
        when(clientRepository.findByDni(clientAdherent.getDni())).thenReturn(clientAdherent);

        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class,
                () -> clientService.getClientAdherentDetails(clientMain.getDni(), clientAdherent.getDni())
        );

        verify(clientRepository, times(2)).findByDni(anyString());
        assertEquals( "The client with DNI " + clientAdherent.getDni() + " is not a adherent of " + clientMain.getDni(), resourceNotFoundException.getMessage());
    }

    @Test @DisplayName("Given DniMain and DniAdherent, when removing client adherent, then delete adherent")
    void givenDniMainAndDniAdherent_whenRemoveClientAdherent_thenDeleteAdherent() {
        Client clientMain = ClientBuilder.buildClientWithAdherentsService();
        Client clientAdherent = clientMain.getAdherents().get(0);
        when(clientRepository.findByDni(clientMain.getDni())).thenReturn(clientMain);
        when(clientRepository.findByDni(clientAdherent.getDni())).thenReturn(clientAdherent);

        clientService.removeClientAdherent(clientMain.getDni(), clientAdherent.getDni());

        verify(clientRepository, times(2)).findByDni(anyString());
        verify(clientRepository, times(1)).deleteById(clientAdherent.getId());
        verify(bankingAccountRepository, times(1)).deleteById(clientAdherent.getBankingAccount().getId());
        verify(clientRepository, times(1)).deleteById(anyLong());
    }

    @Test @DisplayName("Given DniMain, DniAdherent, and RequestUpdateClient, when updating client adherent details, then update client adherent")
    void givenDniMainAndDniAdherentAndRequestUpdateClient_whenUpdateClientAdherentDetails_thenUpdateClientAdherent(){
        Client clientMain = ClientBuilder.buildClientWithAdherentsService();
        Client clientAdherent = clientMain.getAdherents().get(0);
        Client clientAdherentUpdated = Client.builder()
                .name("name test update")
                .address("address test update")
                .build();
        RequestUpdateClient requestUpdateClient = ClientBuilder.buildRequestUpdateClientService();
        when(clientRepository.findByDni(clientMain.getDni())).thenReturn(clientMain);
        when(clientRepository.findByDni(clientAdherent.getDni())).thenReturn(clientAdherent);
        when(clientRepository.save(any(Client.class))).thenReturn(clientAdherentUpdated);

        Client updateClientAdherentDetails = clientService.updateClientAdherentDetails(
                clientMain.getDni(),
                clientAdherent.getDni(),
                requestUpdateClient);

        verify(clientRepository, times(2)).findByDni(anyString());
        verify(clientRepository, times(1)).save(clientAdherent);
        assertEquals("name test update", updateClientAdherentDetails.getName());
        assertEquals("address test update", updateClientAdherentDetails.getAddress());
    }

    @Test @DisplayName("Given DniMain, DniAdherent, and Status, when changing client adherent status, then status is updated")
    void givenDniMainAndDniAdherentAndStatus_whenChangeClientAdherentStatus_thenStatusUpdated() {
        UserStatus newStatus = UserStatus.BANNED;
        Client clientMain = ClientBuilder.buildClientWithAdherentsService();
        Client clientAdherent = clientMain.getAdherents().get(0);
        when(clientRepository.findByDni(clientMain.getDni())).thenReturn(clientMain);
        when(clientRepository.findByDni(clientAdherent.getDni())).thenReturn(clientAdherent);

        clientService.changeClientAdherentStatus(clientMain.getDni(), clientAdherent.getDni(), newStatus);

        assertEquals(newStatus, clientAdherent.getUserStatus());
        verify(clientRepository, times(1)).save(clientAdherent);
    }

}