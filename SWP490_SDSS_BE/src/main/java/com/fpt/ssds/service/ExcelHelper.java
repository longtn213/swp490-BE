package com.fpt.ssds.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ExcelHelper {
    Sheet writeHeaderLine(XSSFWorkbook workbook, String sheetName, List<String> headerName);

    CellStyle createDataFont(XSSFWorkbook workbook);

    void createCell(Sheet sheet, Row row, int columnCount, Object data, CellStyle style);
}
