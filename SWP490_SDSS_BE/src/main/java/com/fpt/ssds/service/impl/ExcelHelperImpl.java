package com.fpt.ssds.service.impl;

import com.fpt.ssds.service.ExcelHelper;
import com.fpt.ssds.utils.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class ExcelHelperImpl implements ExcelHelper {
    @Value("${ssds.config.timezone}")
    String systemTimezone;


    @Override
    public Sheet writeHeaderLine(XSSFWorkbook workbook, String sheetName, List<String> listHeaderName) {
        Sheet sheet = workbook.createSheet(sheetName);
        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12);
        font.setBold(false);
        headerStyle.setFont(font);

        for (int i = 0; i < listHeaderName.size(); i++) {
            Cell headerCell = header.createCell(i);
            headerCell.setCellValue(listHeaderName.get(i));
            headerCell.setCellStyle(headerStyle);
            sheet.autoSizeColumn(i);
        }
        return sheet;
    }

    @Override
    public CellStyle createDataFont(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        style.setFont(font);
        return style;
    }

    @Override
    public void createCell(Sheet sheet, Row row, int columnCount, Object data, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (Objects.isNull(data)) {
            cell.setCellValue("");
        } else {
            if (data instanceof Instant) {
                String datetime = DateUtils.formatInstantToString((Instant) data, systemTimezone, DateUtils.TIME_DATE_FORMAT);
                cell.setCellValue(datetime);
            } else if (data instanceof Boolean) {
                cell.setCellValue((Boolean) data);
            } else if (data instanceof Double) {
                cell.setCellValue((Double) data);
            } else if (data instanceof Boolean) {
                cell.setCellValue((Boolean) data);
            } else {
                cell.setCellValue(String.valueOf(data));
            }
        }
        cell.setCellStyle(style);
    }
}
