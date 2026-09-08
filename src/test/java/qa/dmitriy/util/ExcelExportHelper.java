package qa.dmitriy.util;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class ExcelExportHelper {

    private ExcelExportHelper() {
    }

    public static Workbook openWorkbook(byte[] content) {
        try {
            return new XSSFWorkbook(
                    new ByteArrayInputStream(content)
            );
        } catch (IOException e) {
            throw new AssertionError(
                    "Не удалось открыть XLSX",
                    e
            );
        }
    }

    public static int getDataRowCount(Workbook workbook) {
        return workbook.getSheetAt(0).getLastRowNum();
    }

    public static String getCellValue(
            Workbook workbook,
            int rowIndex,
            int columnIndex) {

        Row row = workbook
                .getSheetAt(0)
                .getRow(rowIndex);

        if (row == null || row.getCell(columnIndex) == null) {
            return "";
        }

        return new DataFormatter()
                .formatCellValue(row.getCell(columnIndex));
    }
}