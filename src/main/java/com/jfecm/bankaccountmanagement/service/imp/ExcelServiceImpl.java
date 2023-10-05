package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.entity.AccountTransaction;
import com.jfecm.bankaccountmanagement.exceptions.ResourceNotFoundException;
import com.jfecm.bankaccountmanagement.service.ExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public byte[] generateAccountTransactionsByDateRangeExcel(List<AccountTransaction> transactions) {

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("Transactions Info");
            XSSFRow row;

            Map<String, Object[]> transactionsInfo = new TreeMap<>();
            transactionsInfo.put("1", new Object[]{"#", "Transaction Type", "Date", "Time", "Amount"});


            if (!transactions.isEmpty()) {
                for (int i = 0; i < transactions.size(); i++) {
                    AccountTransaction transaction = transactions.get(i);
                    transactionsInfo.put(String.valueOf(i + 2), new Object[]{
                            String.valueOf(i + 1),
                            transaction.getAccountTransactionType().toString(),
                            transaction.getDateOfExecution().toString(),
                            transaction.getTimeOfExecution().toString(),
                            "$ " + transaction.getAmount().toString()
                    });
                }
            } else {
                transactionsInfo.put("2", new Object[]{"No transactions found", "", "", ""});
            }

            XSSFCellStyle headerCellStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            XSSFCellStyle dataCellStyle = workbook.createCellStyle();
            XSSFFont dataFont = workbook.createFont();
            dataFont.setColor(IndexedColors.BLACK.getIndex());
            dataCellStyle.setFont(dataFont);

            //Iterate over data and write to sheet
            Set<String> keyid = transactionsInfo.keySet();
            int rowid = 0;

            for (String key : keyid) {
                row = spreadsheet.createRow(rowid++);
                Object[] objectArr = transactionsInfo.get(key);
                int cellid = 0;

                for (Object obj : objectArr) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellValue((String) obj);

                    if (rowid == 1) {
                        cell.setCellStyle(headerCellStyle);
                    } else {
                        cell.setCellStyle(dataCellStyle);
                    }
                }
            }

            for (int i = 0; i < transactionsInfo.get("1").length; i++) {
                spreadsheet.autoSizeColumn(i);
            }

            //Write the workbook in file system
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new ResourceNotFoundException("Problems generating the EXCEL.");
        }

    }
}
