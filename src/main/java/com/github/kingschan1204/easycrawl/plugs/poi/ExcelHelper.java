package com.github.kingschan1204.easycrawl.plugs.poi;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ExcelHelper {

    public static List<Object[]> read(String filePath, int sheetIndex) throws Exception {
        FileInputStream inputStream = new FileInputStream(new File(filePath));
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        List<Object[]> list = new ArrayList<>();

        for (Row row : sheet) {
            List<Object> rowData = new ArrayList<>();
//            Cell cell = row.getCell();
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case NUMERIC:   //数字
                        Double doubleValue = cell.getNumericCellValue();
                        rowData.add(doubleValue);
                        // 格式化科学计数法，取一位整数
//                        DecimalFormat df = new DecimalFormat("0");
//                         = df.format(doubleValue);
                        break;
                    case STRING:    //字符串
                        rowData.add(cell.getStringCellValue());
                        break;
                    case BOOLEAN:   //布尔
                        rowData.add(cell.getBooleanCellValue());
                        break;
                    case BLANK:     // 空值
                        rowData.add(null);
                        break;
                    case FORMULA:   // 公式
                        rowData.add(cell.getCellFormula());
                        break;
                    case ERROR:     // 故障
                        break;
                    default:
                        break;
                }
            }
            list.add(rowData.toArray(new Object[]{}));
        }
        workbook.close();
        inputStream.close();
        return list;

    }

    public static void main(String[] args) throws Exception {
        String file = "C:\\temp\\行业分类.xlsx";
        List<Object[]> list = ExcelHelper.read(file,0);
        list.stream().forEach(r -> {
            Object[] objects = r;
            String s = Arrays.stream(objects).map(String::valueOf).collect(Collectors.joining(","));
            System.out.println(s);
        });
    }
}
