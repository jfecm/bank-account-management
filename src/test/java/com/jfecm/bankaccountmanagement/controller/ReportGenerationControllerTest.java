package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.entity.BankingAccount;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import com.jfecm.bankaccountmanagement.service.ClientService;
import com.jfecm.bankaccountmanagement.service.ExcelService;
import com.jfecm.bankaccountmanagement.service.PdfService;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("ReportGenerationController Tests")
@WebMvcTest(ReportGenerationController.class)
class ReportGenerationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @MockBean
    private BankingAccountService bankingAccountService;

    @MockBean
    private PdfService pdfService;

    @MockBean
    private ExcelService excelService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test @DisplayName("Given client's DNI, when generateAccountDetailsPdf is called, then return a PDF")
    void givenDni_whenGenerateAccountDetailsPdf_thenReturnPDF() throws Exception {
        String urlTemplate = "/api/v1/reports/pdf/client/{dni}/account-details";
        String dni = "123456789";
        Client client = new Client();
        byte[] pdfBytes = "PDF Content".getBytes();

        when(clientService.getClientByDni(dni)).thenReturn(client);
        when(pdfService.generateAccountDetailsPdf(client)).thenReturn(pdfBytes);

        mockMvc.perform(get(urlTemplate, dni))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=AccountDetails_" + dni + "_" + LocalDate.now() + ".pdf"))
                .andExpect(content().bytes(pdfBytes));

        verify(clientService, times(1)).getClientByDni(dni);
        verify(pdfService, times(1)).generateAccountDetailsPdf(client);
    }

    @Test @DisplayName("Given client's DNI, when generateAccountTransactionsPdf is called, then return a PDF")
    void givenDni_whenGenerateAccountTransactionsPdf_thenReturnPDF() throws Exception {
        String urlTemplate = "/api/v1/reports/pdf/client/{dni}/transactions";
        String dni = "123456789";
        Client client = new Client();
        byte[] pdfBytes = "PDF Content".getBytes();

        when(clientService.getClientByDni(dni)).thenReturn(client);
        when(pdfService.generateAccountTransactionsPdf(client)).thenReturn(pdfBytes);

        mockMvc.perform(get(urlTemplate, dni))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=AccountTransactions_" + dni + "_" + LocalDate.now() + ".pdf"))
                .andExpect(content().bytes(pdfBytes));

        verify(clientService, times(1)).getClientByDni(dni);
        verify(pdfService, times(1)).generateAccountTransactionsPdf(client);
    }

    @Test @DisplayName("Given client's DNI, fromDate, and toDate, when generateTransactionsByDateRangeExcel is called, then return an Excel file")
    void givenDniAndDateRange_whenGenerateTransactionsByDateRangeExcel_thenReturnPDF() throws Exception {
        String urlTemplate = "/api/v1/reports/excel/client/{dni}/transactions/filterByDateRange";
        String dni = "123456789";
        LocalDate fromDate = LocalDate.of(2023, 1, 1);
        LocalDate toDate = LocalDate.of(2023, 2, 1);
        BankingAccount bankingAccount = BankingAccount.builder().accountNumber("12345").build();
        Client client = Client.builder().dni(dni).bankingAccount(bankingAccount).build();
        byte[] excelBytes = "Excel Content".getBytes();
        when(clientService.getClientByDni(dni)).thenReturn(client);
        when(bankingAccountService.getAllTransactionsByDateRange(client.getBankingAccount().getAccountNumber(), fromDate, toDate)).thenReturn(Collections.emptyList());
        when(excelService.generateAccountTransactionsByDateRangeExcel(Collections.emptyList())).thenReturn(excelBytes);

        mockMvc.perform(get(urlTemplate, dni)
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Disposition", "attachment; filename=AccountTransactions_Filtered_" + dni + "_FromDate_" + fromDate + "_ToDate_" + toDate + ".xlsx"))
                .andExpect(content().bytes(excelBytes));

        verify(clientService, times(1)).getClientByDni(dni);
        verify(bankingAccountService, times(1)).getAllTransactionsByDateRange(client.getBankingAccount().getAccountNumber(), fromDate, toDate);
    }

    @Test @DisplayName("Given 'fromDate' is after 'toDate', when generateTransactionsByDateRangeExcel is called, then return a Bad Request with an error message")
    void givenDniAndDateRange_whenGenerateTransactionsByDateRangeExcel_thenThrowBadRequest() throws Exception {
        String urlTemplate = "/api/v1/reports/excel/client/{dni}/transactions/filterByDateRange";
        String dni = "123456789";
        LocalDate fromDate = LocalDate.of(2023, 2, 1);
        LocalDate toDate = LocalDate.of(2023, 1, 1);

        mockMvc.perform(get(urlTemplate, dni)
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid date range: 'fromDate' must be before 'toDate'"));
    }


}