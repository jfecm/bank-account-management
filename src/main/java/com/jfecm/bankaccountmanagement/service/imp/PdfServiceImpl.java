package com.jfecm.bankaccountmanagement.service.imp;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.service.PdfService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;


@Service
public class PdfServiceImpl implements PdfService {

    @Override
    public byte[] generateAccountDetailsPdf(Client client) {
        // TODO : COMPLETE
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Creating a Document
        Document document = createEmptyPdfDocument(byteArrayOutputStream);
        // Content

        // Creating an Area Break
        AreaBreak aB = new AreaBreak();
        // Adding area break to the PDF
        document.add(aB);

        // Creating an Area Break
        String para = "Welcome to Tutorialspoint.";
        Paragraph parag = new Paragraph(para);
        // Adding area break to the PDF
        document.add(parag);

        // Creating a list
        List list = new List();
        //  Add elements to the list
        list.add("Java");
        list.add("JavaFX");
        list.add("Apache Tika");
        list.add("OpenCV");
        // Adding list to the document
        document.add(list);


        // Creating a table
        float[] pointColumnWidths = {150F, 150F, 150F};
        Table table = new Table(pointColumnWidths);
        // Adding cells to the table
        table.addCell(new Cell().add(new Paragraph("Name")));
        table.addCell(new Cell().add(new Paragraph("Raju")));
        table.addCell(new Cell().add(new Paragraph("Id")));
        table.addCell(new Cell().add(new Paragraph("1001")));
        table.addCell(new Cell().add(new Paragraph("Designation")));
        table.addCell(new Cell().add(new Paragraph("Programmer")));
        // Adding Table to document
        document.add(table);


        // Creating a table
        float[] pointColumnWidthsTwo = new float[]{200F, 200F};
        Table tableTwo = new Table(pointColumnWidthsTwo);

        // Populating row 1 and adding it to the table
        Cell c1 = new Cell();                        // Creating cell 1
        c1.add(new Paragraph("Name"));                              // Adding name to cell 1
        c1.setBackgroundColor(ColorConstants.DARK_GRAY);      // Setting background color
        c1.setBorder(Border.NO_BORDER);              // Setting border
        c1.setTextAlignment(TextAlignment.CENTER);   // Setting text alignment
        tableTwo.addCell(c1);                           // Adding cell 1 to the table

        Cell c2 = new Cell();
        c2.add(new Paragraph("Raju"));
        c2.setBackgroundColor(ColorConstants.GRAY);
        c2.setBorder(Border.NO_BORDER);
        c2.setTextAlignment(TextAlignment.CENTER);
        tableTwo.addCell(c2);

        // Populating row 2 and adding it to the table
        Cell c3 = new Cell();
        c3.add(new Paragraph("Id"));
        c3.setBackgroundColor(ColorConstants.WHITE);
        c3.setBorder(Border.NO_BORDER);
        c3.setTextAlignment(TextAlignment.CENTER);
        tableTwo.addCell(c3);

        Cell c4 = new Cell();
        c4.add(new Paragraph("001"));
        c4.setBackgroundColor(ColorConstants.WHITE);
        c4.setBorder(Border.NO_BORDER);
        c4.setTextAlignment(TextAlignment.CENTER);
        tableTwo.addCell(c4);

        // Populating row 3 and adding it to the table
        Cell c5 = new Cell();
        c5.add(new Paragraph("Designation"));
        c5.setBackgroundColor(ColorConstants.DARK_GRAY);
        c5.setBorder(Border.NO_BORDER);
        c5.setTextAlignment(TextAlignment.CENTER);
        tableTwo.addCell(c5);

        Cell c6 = new Cell();
        c6.add(new Paragraph("Programmer"));
        c6.setBackgroundColor(ColorConstants.GRAY);
        c6.setBorder(Border.NO_BORDER);
        c6.setTextAlignment(TextAlignment.CENTER);
        tableTwo.addCell(c6);

        // Adding Table to document
        document.add(tableTwo);


        // Closing the document
        document.close();
        return byteArrayOutputStream.toByteArray();
    }


    @Override
    public byte[] generateAccountTransactionsPdf(Client client) {
        // TODO : COMPLETE

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // Creating a Document
        Document document = createEmptyPdfDocument(byteArrayOutputStream);
        // Content

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
}
