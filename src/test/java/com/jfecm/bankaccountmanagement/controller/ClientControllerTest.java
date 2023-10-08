package com.jfecm.bankaccountmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.service.ClientService;
import com.jfecm.bankaccountmanagement.service.EmailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("ClientController Tests")
@WebMvcTest(ClientController.class)
class ClientControllerTest {
    @MockBean
    private ClientService clientService;
    @MockBean
    private EmailService emailService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test @DisplayName("Given valid RequestCreateClient, when createClient is called, then return success response")
    void givenRequestCreateClient_whenCreateClient_thenSaveClient() throws Exception {
        String urlTemplate = "/api/v1/clients/client";
        Client client = Client.builder()
                .id(1L)
                .dni("123456789")
                .name("test name")
                .email("test@example.com")
                .password("test password")
                .address("test address")
                .build();
        when(clientService.saveClient(any(RequestCreateClient.class))).thenReturn(client);

        mockMvc.perform(post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result", is("Client created and email sent.")))
                .andExpect(jsonPath("$.Data", notNullValue()));

        verify(clientService, times(1)).saveClient(any(RequestCreateClient.class));
    }

    @Test @DisplayName("Given an error in sending email, when createClient is called, then return an internal server error response")
    void givenRequestCreateClient_whenCreateClient_thenThrowInternalServerError() throws Exception {
        String urlTemplate = "/api/v1/clients/client";
        Client client = Client.builder()
                .id(1L)
                .dni("123456789")
                .name("test name")
                .email("test@example.com")
                .password("test password")
                .address("test address")
                .build();
        doThrow(new MessagingException()).when(emailService).sendEmail(anyString(), anyString(), any());

        mockMvc.perform(post(urlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(client)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.Error", is("An error occurred while sending the email.")));

        verify(clientService, times(1)).saveClient(any(RequestCreateClient.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), any());
    }

    @Test @DisplayName("Given active clients, when getAllClients is called, then return a list of active clients")
    void givenUserStatus_whenGetAllClients_thenReturnsListActiveClients() throws Exception {
        String urlTemplate = "/api/v1/clients";
        List<ResponseClientData> clients = List.of(
                ResponseClientData.builder().id(1L).dni("123").userStatus(UserStatus.ACTIVE).build(),
                ResponseClientData.builder().id(2L).dni("321").userStatus(UserStatus.ACTIVE).build()
        );
        when(clientService.getAllClients(UserStatus.ACTIVE)).thenReturn(clients);

        mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Total").value(2))
                .andExpect(jsonPath("$.Result[0].id").value(1))
                .andExpect(jsonPath("$.Result[0].dni").value("123"))
                .andExpect(jsonPath("$.Result[0].userStatus").value("ACTIVE"));
    }

    @Test @DisplayName("Given valid DNI, when getClientByDni is called, then return client details")
    void testGetClientByDni() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}";
        String dni = "123456789";
        Client client = Client.builder()
                .id(1L)
                .dni(dni)
                .name("test name")
                .email("test@example.com")
                .password("test password")
                .address("test address")
                .build();
        when(clientService.getClientByDni(anyString())).thenReturn(client);

        mockMvc.perform(get(urlTemplate, dni)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id").value(1))
                .andExpect(jsonPath("$.Result.dni").value(dni));

        verify(clientService, times(1)).getClientByDni(anyString());
    }

    @Test @DisplayName("Given valid DNI, when deleteClientByDni is called, then return success response")
    void testDeleteClientByDni() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}";
        String dni = "123456789";

        mockMvc.perform(delete(urlTemplate, dni)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Client Deleted"));

        verify(clientService, times(1)).deleteClientByDni(dni);
    }

    @Test @DisplayName("Given valid DNI and updated client data, when updateClientByDni is called, then return updated client")
    void testUpdateClientByDni() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}";
        String dni = "123456789";
        RequestUpdateClient newDataClient = RequestUpdateClient.builder().name("New Name").address("New Address").build();
        Client updatedClient = Client.builder().dni(dni).name("New Name").address("New Address").build();
        when(clientService.updateClientByDni(eq(dni), any(RequestUpdateClient.class))).thenReturn(updatedClient);

        mockMvc.perform(put(urlTemplate, dni)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDataClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.dni").value(dni))
                .andExpect(jsonPath("$.Result.name").value(newDataClient.getName()))
                .andExpect(jsonPath("$.Result.address").value(newDataClient.getAddress()));

        verify(clientService, times(1)).updateClientByDni(eq(dni), any(RequestUpdateClient.class));
    }

    @Test @DisplayName("Given valid DNI and new user status, when updateClientStatusByDni is called, then return success response")
    void testUpdateClientStatusByDni() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}/status/{status}";
        String dni = "123456789";
        UserStatus newStatus = UserStatus.INACTIVE;
        doNothing().when(clientService).updateClientStatusByDni(dni, newStatus);

        mockMvc.perform(put(urlTemplate, dni, newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Updated client status"));

        verify(clientService, times(1)).updateClientStatusByDni(dni, newStatus);
    }

    @Test @DisplayName("Given valid DNI and adherent data, when createClientAdherent is called, then return adherent details")
    void testCreateClientAdherent() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}/adherents/adherent";
        String dni = "123456789";
        RequestCreateClient adherentRequest = RequestCreateClient.builder().name("Adherent Name").build();
        Client adherent = Client.builder().id(2L).name("Adherent Name").build();
        when(clientService.addClientAdherent(dni, adherentRequest)).thenReturn(adherent);

        mockMvc.perform(post(urlTemplate, dni)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adherentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Client adherent created."))
                .andExpect(jsonPath("$.Adherent.id").value(2))
                .andExpect(jsonPath("$.Adherent.name").value("Adherent Name"));

        verify(clientService).addClientAdherent(dni, adherentRequest);
    }

    @Test @DisplayName("Given valid DNI, when getAdherents is called, then return list of adherents")
    void testGetAdherents() throws Exception {
        String urlTemplate = "/api/v1/clients/client/{dni}/adherents";
        String dni = "123456789";
        List<Client> adherents = Arrays.asList(
                Client.builder().id(1L).name("Adherent 1").build(),
                Client.builder().id(2L).name("Adherent 2").build()
        );
        when(clientService.getClientAdherentsList(anyString())).thenReturn(adherents);

        mockMvc.perform(get(urlTemplate, dni)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").isArray())
                .andExpect(jsonPath("$.Result[0].id").value(1))
                .andExpect(jsonPath("$.Result[0].name").value("Adherent 1"))
                .andExpect(jsonPath("$.Result[1].id").value(2))
                .andExpect(jsonPath("$.Result[1].name").value("Adherent 2"));

        verify(clientService, times(1)).getClientAdherentsList(anyString());
    }

    @Test @DisplayName("Given valid DNI and DNI Adherent, when getAdherent is called, then return adherent details")
    void testGetAdherent() throws Exception {
        String dniMain = "123456789";
        String dniAdherent = "987654321";
        String urlTemplate = "/api/v1/clients/client/{dniMain}/adherents/adherent/{dniAdherent}";

        Client adherent = Client.builder()
                .id(2L)
                .dni(dniAdherent)
                .name("Adherent Name")
                .build();

        when(clientService.getClientAdherentDetails(dniMain, dniAdherent)).thenReturn(adherent);

        mockMvc.perform(get(urlTemplate, dniMain, dniAdherent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result.id").value(2))
                .andExpect(jsonPath("$.Result.dni").value(dniAdherent))
                .andExpect(jsonPath("$.Result.name").value("Adherent Name"));

        verify(clientService, times(1)).getClientAdherentDetails(dniMain, dniAdherent);
    }

    @Test @DisplayName("Given valid DNI and DNI Adherent, when deleteAdherent is called, then return success response")
    void testDeleteAdherent() throws Exception {
        String dniMain = "123456789";
        String dniAdherent = "987654321";
        String urlTemplate = "/api/v1/clients/client/{dniMain}/adherents/adherent/{dniAdherent}";

        mockMvc.perform(delete(urlTemplate, dniMain, dniAdherent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Client adherent deleted."));

        verify(clientService, times(1)).removeClientAdherent(dniMain, dniAdherent);
    }

    @Test @DisplayName("Given valid DNI, DNI Adherent, and updated adherent data, when updateAdherent is called, then return updated adherent")
    void testUpdateAdherent() throws Exception {
        String dniMain = "123456789";
        String dniAdherent = "987654321";
        String urlTemplate = "/api/v1/clients/client/{dniMain}/adherents/adherent/{dniAdherent}";

        RequestUpdateClient adherentRequest = RequestUpdateClient.builder()
                .name("Updated Adherent Name")
                .build();

        Client updatedAdherent = Client.builder()
                .id(2L)
                .name("Updated Adherent Name")
                .build();

        when(clientService.updateClientAdherentDetails(dniMain, dniAdherent, adherentRequest)).thenReturn(updatedAdherent);

        mockMvc.perform(put(urlTemplate, dniMain, dniAdherent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adherentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value(updatedAdherent));

        verify(clientService, times(1)).updateClientAdherentDetails(dniMain, dniAdherent, adherentRequest);
    }

    @Test @DisplayName("Given valid DNI, DNI Adherent, and new user status, when updateAdherentStatus is called, then return success response")
    void testUpdateAdherentStatus() throws Exception {
        String dniMain = "123456789";
        String dniAdherent = "987654321";
        String urlTemplate = "/api/v1/clients/client/{dniMain}/adherents/adherent/{dniAdherent}/status/{status}";
        UserStatus newStatus = UserStatus.INACTIVE;

        mockMvc.perform(put(urlTemplate, dniMain, dniAdherent, newStatus)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Result").value("Updated client adherent status."));

        verify(clientService, times(1)).changeClientAdherentStatus(dniMain, dniAdherent, newStatus);
    }

}