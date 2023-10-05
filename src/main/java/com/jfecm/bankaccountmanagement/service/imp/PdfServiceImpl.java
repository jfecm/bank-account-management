package com.jfecm.bankaccountmanagement.service.imp;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.entity.enums.AccountTransactionType;
import com.jfecm.bankaccountmanagement.entity.enums.BankingAccountStatus;
import com.jfecm.bankaccountmanagement.entity.enums.UserStatus;
import com.jfecm.bankaccountmanagement.exceptions.ResourceNotFoundException;
import com.jfecm.bankaccountmanagement.service.PdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Service
public class PdfServiceImpl implements PdfService {
    @Override
    public byte[] generateAccountTransactionsPdf(Client client) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = createEmptyPdfDocument(byteArrayOutputStream);
        // Content
        try {
            createHeaderFooter(document);
            createClientDetails(document, client);
            createAccountTransactionsDetails(document, client);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Problems generating the PDF.");
        }
        // Closing the document
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private Color getTransactionColor(AccountTransactionType accountTransactionType) {
        switch (accountTransactionType) {
            case RECHARGE:
                return new DeviceRgb(0, 128, 0);
            case WITHDRAWAL:
                return new DeviceRgb(255, 0, 0);
            case TRANSFER:
                return new DeviceRgb(0, 0, 128);
            default:
                return ColorConstants.BLACK;
        }
    }

    @Override
    public byte[] generateAccountDetailsPdf(Client client) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = createEmptyPdfDocument(byteArrayOutputStream);
        // Content
        try {
            createHeaderFooter(document);
            createClientDetails(document, client);
            createAccountDetails(document, client);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Problems generating the PDF.");
        }

        // Closing the document
        document.close();
        return byteArrayOutputStream.toByteArray();
    }

    private Document createEmptyPdfDocument(ByteArrayOutputStream byteArrayOutputStream) {
        // Creating a PdfWriter
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        // Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // Adding a new page
        pdfDoc.addNewPage();
        // Creating a Document
        return new Document(pdfDoc, PageSize.A4, false);
    }

    private void createHeaderFooter(Document document) throws IOException {
        String footerContent = "@ JFECM Software, All Rights Reserved. \n This is not meant to be something serious.";
        String headerContent = "<BANK_NAME> by @jfecm\n<BANK_EMAIL> - 0123456789 - ADDRESS - CITY - COUNTRY - POSTCODE - STATE";

        Paragraph head = new Paragraph(headerContent)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontColor(ColorConstants.DARK_GRAY)
                .setFontSize(10)
                .setBackgroundColor(new DeviceRgb(230, 161, 255));

        Paragraph footer = new Paragraph(footerContent)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontColor(ColorConstants.DARK_GRAY)
                .setFontSize(8).setBackgroundColor(new DeviceRgb(237, 180, 228));

        int numberOfPages = document.getPdfDocument().getNumberOfPages();

        for (int i = 1; i <= numberOfPages; i++) {
            Rectangle pageSize = document.getPdfDocument().getPage(i).getPageSize();

            float x = pageSize.getWidth() / 2;
            float y = pageSize.getTop();

            document.showTextAligned(head, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, 0);

            x = 300;
            y = 0;

            document.showTextAligned(footer, x, y, i, TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);
        }

    }

    private void createClientDetails(Document document, Client client) throws IOException {
        Paragraph c = createTitleParagraphOf("Client Details");

        document.add(c);

        Paragraph clientDetails = new Paragraph()
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12);

        clientDetails.add(new Text("DNI: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        clientDetails.add(new Text(client.getDni()).setFontColor(ColorConstants.BLACK));
        clientDetails.add("\n");

        clientDetails.add(new Text("Name: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        clientDetails.add(new Text(client.getName()).setFontColor(ColorConstants.BLACK));
        clientDetails.add("\n");

        clientDetails.add(new Text("Email: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        clientDetails.add(new Text(client.getEmail()).setFontColor(ColorConstants.BLACK));
        clientDetails.add("\n");

        clientDetails.add(new Text("Address: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        clientDetails.add(new Text(client.getAddress()).setFontColor(ColorConstants.BLACK));
        clientDetails.add("\n");

        clientDetails.add(new Text("Client Status: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        boolean statusClient = client.getUserStatus() != UserStatus.ACTIVE;
        clientDetails.add(new Text(client.getUserStatus().toString()).setFontColor(statusClient ? ColorConstants.RED : ColorConstants.BLACK));
        clientDetails.add("\n");

        document.add(clientDetails);
    }

    private void createAccountDetails(Document document, Client client) throws IOException {
        Paragraph accountParagraph = createTitleParagraphOf("Account Details");

        document.add(accountParagraph);

        Paragraph accountDetails = new Paragraph()
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(12);

        accountDetails.add(new Text("Account Number: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        accountDetails.add(new Text(client.getBankingAccount().getAccountNumber()).setFontColor(ColorConstants.BLACK));
        accountDetails.add("\n");

        accountDetails.add(new Text("Account Status: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        boolean statusAccount = client.getBankingAccount().getBankingAccountStatus() != BankingAccountStatus.ACTIVE;
        accountDetails.add(new Text(client.getBankingAccount().getBankingAccountStatus().toString()).setFontColor(statusAccount ? ColorConstants.RED : ColorConstants.BLACK));
        accountDetails.add("\n");

        accountDetails.add(new Text("Account Opened Date: ").setFontColor(ColorConstants.DARK_GRAY).setBold());
        accountDetails.add(new Text(client.getBankingAccount().getAccountOpenedDate().toString()).setFontColor(ColorConstants.BLACK));
        accountDetails.add("\n");

        accountDetails.add(new Text("Withdrawal Limit: $").setFontColor(ColorConstants.DARK_GRAY).setBold());
        accountDetails.add(new Text(client.getBankingAccount().getWithdrawalLimit().toString()).setFontColor(ColorConstants.BLACK));
        accountDetails.add("\n");

        accountDetails.add(new Text("Balance: $").setFontColor(ColorConstants.DARK_GRAY).setBold());
        accountDetails.add(new Text(client.getBankingAccount().getBalance().toString()).setFontColor(ColorConstants.BLACK));
        accountDetails.add("\n");

        document.add(accountDetails);
    }

    private void createAccountTransactionsDetails(Document document, Client client) throws IOException {
        Paragraph paragraph = createTitleParagraphOf("Account Transactions");
        document.add(paragraph);

        List<AccountTransaction> transactions = client.getBankingAccount().getAccountTransactions();

        if (transactions.isEmpty()) {
            Paragraph noTransactionsMessage = new Paragraph("No transactions available for this account.")
                    .setFontColor(ColorConstants.RED)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(noTransactionsMessage);
        } else {
            int numTransactionsToShow = Math.min(transactions.size(), 20);

            Table table = new Table(4);
            table.setWidth(500);
            table.setTextAlignment(TextAlignment.CENTER);

            table.addHeaderCell("Transaction Type");
            table.addHeaderCell("Date");
            table.addHeaderCell("Time");
            table.addHeaderCell("Amount");

            for (int i = 0; i < numTransactionsToShow; i++) {
                AccountTransaction transaction = transactions.get(i);

                Cell typeCell = new Cell().add(
                                new Paragraph(transaction.getAccountTransactionType().toString()))
                        .setFontColor(ColorConstants.WHITE)
                        .setBackgroundColor(getTransactionColor(transaction.getAccountTransactionType()));
                table.addCell(typeCell);

                table.addCell(transaction.getDateOfExecution().toString());
                table.addCell(transaction.getTimeOfExecution().toString());
                table.addCell("$ " + transaction.getAmount().toString());
            }

            document.add(table);
        }

    }


    private Paragraph createTitleParagraphOf(String content) throws IOException {
        return new Paragraph(content)
                .setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN))
                .setFontColor(ColorConstants.BLACK)
                .setFontSize(15)
                .setUnderline(1f, -3f);
    }
}
