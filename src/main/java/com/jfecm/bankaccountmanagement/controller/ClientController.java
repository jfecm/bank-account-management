package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.service.ClientService;
import com.jfecm.bankaccountmanagement.service.EmailService;
import com.jfecm.bankaccountmanagement.util.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;
    private final EmailService emailService;

    /**
     * Endpoint to create a client.
     *
     * @param client The client data to create.
     * @return ResponseEntity with the creation result.
     */
    @PostMapping("/client")
    public ResponseEntity<Map<String, Object>> createClient(@RequestBody RequestCreateClient client) {
        try {
            Client clientCreated = clientService.saveClient(client);
            emailService.sendEmail(client.getEmail(), "Welcome message", Email.welcomeMessage(client));
            return ResponseEntity.ok(Map.of("Result", "Client created and email sent.", "Data", clientCreated));
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(Map.of("Error", "An error occurred while sending the email."));
        }
    }

    /**
     * Endpoint to obtain clients.
     *
     * @param status The status of the clients to get (default: "ACTIVE").
     * @return ResponseEntity with the list of customers.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllClients(@RequestParam(required = false, defaultValue = "ACTIVE") UserStatus status) {
        List<ResponseClientData> clientList = clientService.getAllClients(status);
        return new ResponseEntity<>(Map.of("Total", clientList.size(), "Result", clientList), HttpStatus.OK);
    }

    /**
     * Endpoint to obtain a client by DNI.
     *
     * @param dni The DNI of the client to obtain.
     * @return ResponseEntity with the found client.
     */
    @GetMapping("/client/{dni}")
    public ResponseEntity<Map<String, Object>> getClientByDni(@PathVariable String dni) {
        Client client = clientService.getClientByDni(dni);
        return ResponseEntity.ok(Map.of("Result", client));
    }

    /**
     * Endpoint to delete (change status) a client by DNI.
     *
     * @param dni The DNI of the client to delete.
     * @return ResponseEntity with the result of the deletion.
     */
    @DeleteMapping("/client/{dni}")
    public ResponseEntity<Map<String, Object>> deleteClientByDni(@PathVariable String dni) {
        clientService.deleteClientByDni(dni);
        return ResponseEntity.ok(Map.of("Result", "Client Deleted"));
    }

    /**
     * Endpoint to update a client by DNI.
     *
     * @param dni           The DNI of the client to update.
     * @param newDataClient The new data for the client.
     * @return ResponseEntity with the updated client.
     */
    @PutMapping("/client/{dni}")
    public ResponseEntity<Map<String, Object>> updateClientByDni(@PathVariable String dni,
                                                                 @RequestBody RequestUpdateClient newDataClient) {
        Client client = clientService.updateClientByDni(dni, newDataClient);
        return ResponseEntity.ok(Map.of("Result", client));
    }

    /**
     * Endpoint to update the status of a client by DNI.
     *
     * @param dni    The DNI of the client to update.
     * @param status The new status for the client.
     * @return ResponseEntity with the updated client.
     */
    @PutMapping("/client/{dni}/status/{status}")
    public ResponseEntity<Map<String, Object>> updateClientStatusByDni(@PathVariable String dni,
                                                                       @PathVariable UserStatus status) {
        clientService.updateClientStatusByDni(dni, status);
        return ResponseEntity.ok(Map.of("Result", "Updated client status"));
    }
    /**
     * Creates a new adherent client for a main client identified by their DNI.
     *
     * @param dni              The DNI of the main client.
     * @param adherentRequest  The data of the adherent client to be created.
     * @return A response containing the result and the ID of the created adherent client.
     */
    @PostMapping("/client/{dni}/adherents/adherent")
    public ResponseEntity<Map<String, Object>> createClientAdherent(@PathVariable String dni, @RequestBody RequestCreateClient adherentRequest) {
        Client adherent = clientService.addClientAdherent(dni, adherentRequest);
        return ResponseEntity.ok(Map.of("Result", "Client adherent created.", "Adherent", adherent));
    }

    /**
     * Retrieves the list of adherent clients for a main client identified by their DNI.
     *
     * @param dni The DNI of the main client.
     * @return A response containing the list of adherent clients.
     */
    @GetMapping("/client/{dni}/adherents")
    public ResponseEntity<Map<String, Object>> getAdherents(@PathVariable String dni) {
        List<Client> adherents = clientService.getClientAdherentsList(dni);
        return ResponseEntity.ok(Map.of("Result", adherents));
    }

    /**
     * Retrieves the details of an adherent client for a main client identified by their DNIs.
     *
     * @param dniMain      The DNI of the main client.
     * @param dniAdherent  The DNI of the adherent client to retrieve.
     * @return A response containing the details of the adherent client.
     */
    @GetMapping("/client/{dniMain}/adherents/adherent/{dniAdherent}")
    public ResponseEntity<Map<String, Object>> getAdherent(@PathVariable String dniMain, @PathVariable String dniAdherent) {
        Client adherent = clientService.getClientAdherentDetails(dniMain, dniAdherent);
        return ResponseEntity.ok(Map.of("Result", adherent));
    }

    /**
     * Deletes an adherent client from a main client's list of adherents.
     *
     * @param dniMain      The DNI of the main client.
     * @param dniAdherent  The DNI of the adherent client to delete.
     * @return A response indicating the result of the deletion.
     */
    @DeleteMapping("/client/{dniMain}/adherents/adherent/{dniAdherent}")
    public ResponseEntity<Map<String, Object>> deleteAdherent(@PathVariable String dniMain, @PathVariable String dniAdherent) {
        clientService.removeClientAdherent(dniMain, dniAdherent);
        return ResponseEntity.ok(Map.of("Result", "Client adherent deleted."));
    }

    /**
     * Updates the details of an adherent client for a main client identified by their DNIs.
     *
     * @param dniMain          The DNI of the main client.
     * @param dniAdherent      The DNI of the adherent client to update.
     * @param adherentRequest  The new data for the adherent client.
     * @return A response containing the updated adherent client.
     */
    @PutMapping("/client/{dniMain}/adherents/adherent/{dniAdherent}")
    public ResponseEntity<Map<String, Object>> updateAdherent(@PathVariable String dniMain,
                                                              @PathVariable String dniAdherent,
                                                              @RequestBody RequestUpdateClient adherentRequest) {
        Client client = clientService.updateClientAdherentDetails(dniMain, dniAdherent, adherentRequest);
        return ResponseEntity.ok(Map.of("Result", client));
    }

    /**
     * Updates the status of an adherent client for a main client identified by their DNIs.
     *
     * @param dniMain      The DNI of the main client.
     * @param dniAdherent  The DNI of the adherent client whose status will be changed.
     * @param status       The new status for the adherent client.
     * @return A response indicating the result of the status update.
     */
    @PutMapping("/client/{dniMain}/adherents/adherent/{dniAdherent}/status/{status}")
    public ResponseEntity<Map<String, Object>> updateAdherentStatus(@PathVariable String dniMain,
                                                                    @PathVariable String dniAdherent,
                                                                    @PathVariable UserStatus status) {
        clientService.changeClientAdherentStatus(dniMain, dniAdherent, status);
        return ResponseEntity.ok(Map.of("Result", "Updated client adherent status."));
    }

}
