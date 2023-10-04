package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.dto.request.RequestCreateClient;
import com.jfecm.bankaccountmanagement.dto.request.RequestUpdateClient;
import com.jfecm.bankaccountmanagement.dto.response.ResponseClientData;
import com.jfecm.bankaccountmanagement.entity.Client;
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
            clientService.saveClient(client);
            emailService.sendEmail(client.getEmail(), "Welcome message", Email.welcomeMessage(client));
            return ResponseEntity.ok(Map.of("Result", "Client Created."));
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
    public ResponseEntity<Map<String, Object>> getAllClients(@RequestParam(required = false, defaultValue = "ACTIVE") String status) {
        List<ResponseClientData> clientList = clientService.getAllClients(status);

        if (clientList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

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
                                                                       @PathVariable String status) {
        clientService.updateClientStatusByDni(dni, status);
        return ResponseEntity.ok(Map.of("Result", "Updated client status"));
    }
}
