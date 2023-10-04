package com.jfecm.bankaccountmanagement.service;

import com.jfecm.bankaccountmanagement.entity.Client;

// Using 'iText' Java PDF library that create, convert, and manipulate PDF documents.
public interface PdfService {
    byte[] generateAccountDetailsPdf(Client client);

    byte[] generateAccountTransactionsPdf(Client client);
}
