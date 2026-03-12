package com.railway.common.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ExcelUtils {

  private ExcelUtils() {}

  public static String getString(Row row, int colIndex) {
    Cell cell = row.getCell(colIndex);
    if (cell == null) return null;
    if (cell.getCellType() == CellType.STRING) {
      String val = cell.getStringCellValue().trim();
      return val.isBlank() ? null : val;
    }
    if (cell.getCellType() == CellType.NUMERIC) {
      return String.valueOf((long) cell.getNumericCellValue()).trim();
    }
    return null;
  }

  public static Integer getInteger(Row row, int colIndex) {
    Cell cell = row.getCell(colIndex);
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) {
      return (int) cell.getNumericCellValue();
    }
    if (cell.getCellType() == CellType.STRING) {
      try {
        return Integer.parseInt(cell.getStringCellValue().trim());
      } catch (NumberFormatException e) {
        return null;
      }
    }
    return null;
  }

  public static boolean isRowEmpty(Row row) {
    if (row == null) return true;
    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        return false;
      }
    }
    return true;
  }
}
