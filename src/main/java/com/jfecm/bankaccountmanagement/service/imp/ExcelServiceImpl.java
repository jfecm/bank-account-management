package com.jfecm.bankaccountmanagement.service.imp;

import com.jfecm.bankaccountmanagement.entity.Client;
import com.jfecm.bankaccountmanagement.service.ExcelService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public byte[] generateAccountTransactionsByDateRangeExcel(Client client) {
        // TODO : COMPLETE
        try {
            //Create blank workbook
            XSSFWorkbook workbook = new XSSFWorkbook();

            //Create a blank sheet
            XSSFSheet spreadsheet = workbook.createSheet(" Employee Info ");

            //Create row object
            XSSFRow row;

            //This data needs to be written (Object[])
            Map<String, Object[]> empinfo = new TreeMap<>();
            empinfo.put("1", new Object[]{"EMP ID", "EMP NAME", "DESIGNATION"});
            empinfo.put("2", new Object[]{"tp01", "Gopal", "Technical Manager"});
            empinfo.put("3", new Object[]{"tp02", "Manisha", "Proof Reader"});
            empinfo.put("4", new Object[]{"tp03", "Eli", "Technical Writer rr"});
            empinfo.put("5", new Object[]{"tp04", "Satish", "Technical Writer"});
            empinfo.put("6", new Object[]{"tp05", "Krishna", "Technical Writer"});

            //Iterate over data and write to sheet
            Set<String> keyid = empinfo.keySet();
            int rowid = 0;

            for (String key : keyid) {
                row = spreadsheet.createRow(rowid++);
                Object[] objectArr = empinfo.get(key);
                int cellid = 0;

                for (Object obj : objectArr) {
                    Cell cell = row.createCell(cellid++);
                    cell.setCellValue((String) obj);
                }
            }
            //Write the workbook in file system
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            workbook.write(byteArrayOutputStream);
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
