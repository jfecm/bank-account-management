package com.jfecm.bankaccountmanagement.controller;

import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.service.BankingAccountService;
import com.jfecm.bankaccountmanagement.service.ClientService;
import com.jfecm.bankaccountmanagement.service.ExcelService;
import com.jfecm.bankaccountmanagement.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/reports")
public class ReportGenerationController {
    private final ClientService clientService;
    private final BankingAccountService bankingAccountService;
    private final PdfService pdfService;
    private final ExcelService excelService;

    /**
     * Generates a PDF with account details for a client.
     *
     * @param dni The client's identification number.
     * @return An HTTP response containing the generated PDF.
     */
    @GetMapping(value = "/pdf/client/{dni}/account-details", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateAccountDetailsPdf(@PathVariable String dni) {
        // Get the client corresponding to the provided DNI number
        Client client = clientService.getClientByDni(dni);
        // Generate the PDF for account details
        byte[] bis = pdfService.generateAccountDetailsPdf(client);
        // Create the PDF file name with the DNI and current date
        String filename = "AccountDetails_" + dni + "_" + LocalDate.now() + ".pdf";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(bis);
    }

    /**
     * Generates a PDF with account transactions for a client.
     *
     * @param dni The client's identification number.
     * @return An HTTP response containing the PDF of the transactions.
     */
    @GetMapping(value = "/pdf/client/{dni}/transactions", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateAccountTransactionsPdf(@PathVariable String dni) {
        // Get the client corresponding to the provided DNI number
        Client client = clientService.getClientByDni(dni);
        // Generate the PDF for account transactions
        byte[] bis = pdfService.generateAccountTransactionsPdf(client);
        // Create the PDF file name with the DNI and current date
        String filename = "AccountTransactions_" + dni + "_" + LocalDate.now() + ".pdf";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_PDF).body(bis);
    }

    /**
     * Generates an Excel file with account transactions for a client, filtered by date range.
     *
     * @param dni      The client's identification number.
     * @param fromDate The start date of the date range.
     * @param toDate   The end date of the date range.
     * @return An HTTP response containing the Excel file with filtered transactions.
     */
    @GetMapping(value = "/excel/client/{dni}/transactions/filterByDateRange", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generateTransactionsByDateRangeExcel(@PathVariable String dni,
                                                                       @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                                       @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            String errorMessage = "Invalid date range: 'fromDate' must be before 'toDate'";
            return ResponseEntity.badRequest().body(errorMessage.getBytes());
        }

        // Get the client corresponding to the provided DNI number
        Client client = clientService.getClientByDni(dni);
        List<AccountTransaction> transactions = bankingAccountService.getAllTransactionsByDateRange(client.getBankingAccount().getAccountNumber(), fromDate, toDate);

        // Generate the Excel file with transactions filtered by date range
        byte[] bis = excelService.generateAccountTransactionsByDateRangeExcel(transactions);
        // Create the Excel file name with the DNI and date range
        String filename = "AccountTransactions_Filtered_" + dni + "_FromDate_" + fromDate + "_ToDate_" + toDate + ".xlsx";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok().headers(httpHeaders).contentType(MediaType.APPLICATION_OCTET_STREAM).body(bis);
    }
}
