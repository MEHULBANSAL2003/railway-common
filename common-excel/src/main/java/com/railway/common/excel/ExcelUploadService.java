package com.railway.common.excel;

import com.railway.common.exceptions.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class ExcelUploadService {

  private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final int HEADER_ROW_INDEX = 0;
  private static final int DATA_START_ROW_INDEX = 1;

  /**
   * Generic Excel processor.
   *
   * @param file       uploaded .xlsx file
   * @param mapper     entity-specific row mapper
   * @param processor  what to do with each successfully mapped row (e.g. service.create)
   * @param <T>        target type
   * @return ExcelUploadResult with success/failure summary
   */
  public <T> ExcelUploadResult process(
    MultipartFile file,
    ExcelRowMapper<T> mapper,
    Consumer<T> processor
  ) {
    validateFile(file);

    List<ExcelUploadResult.RowError> errors = new ArrayList<>();
    int successCount = 0;
    int totalRows = 0;

    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);

      validateHeaders(sheet.getRow(HEADER_ROW_INDEX), mapper.expectedHeaders());

      for (int i = DATA_START_ROW_INDEX; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);

        if (ExcelUtils.isRowEmpty(row)) continue;

        totalRows++;
        int rowNumber = i + 1; // 1-indexed for user-facing errors

        try {
          T mapped = mapper.map(row, rowNumber);
          processor.accept(mapped);
          successCount++;
        } catch (IllegalArgumentException ex) {
          // Row mapping validation failure
          errors.add(ExcelUploadResult.RowError.builder()
            .rowNumber(rowNumber)
            .reason(ex.getMessage())
            .build());
        } catch (BaseException ex) {
          // Business logic failure (e.g. duplicate)
          errors.add(ExcelUploadResult.RowError.builder()
            .rowNumber(rowNumber)
            .reason(ex.getMessage())
            .build());
        } catch (Exception ex) {
          log.error("Unexpected error processing row {}", rowNumber, ex);
          errors.add(ExcelUploadResult.RowError.builder()
            .rowNumber(rowNumber)
            .reason("Unexpected error processing this row")
            .build());
        }
      }

    } catch (IOException ex) {
      throw new BaseException(HttpStatus.BAD_REQUEST, "EXCEL_READ_ERROR", "Failed to read Excel file");
    }

    log.info("Excel upload complete. Total: {}, Success: {}, Failed: {}",
      totalRows, successCount, errors.size());

    return ExcelUploadResult.builder()
      .totalRows(totalRows)
      .successCount(successCount)
      .failedCount(errors.size())
      .errors(errors)
      .build();
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BaseException(HttpStatus.BAD_REQUEST, "EXCEL_EMPTY", "Uploaded file is empty");
    }
    if (!EXCEL_CONTENT_TYPE.equals(file.getContentType())) {
      throw new BaseException(HttpStatus.BAD_REQUEST, "EXCEL_INVALID_FORMAT", "Only .xlsx files are supported");
    }
  }

  private void validateHeaders(Row headerRow, String[] expectedHeaders) {
    if (headerRow == null) {
      throw new BaseException(HttpStatus.BAD_REQUEST, "EXCEL_MISSING_HEADERS", "Excel file has no header row");
    }
    for (int i = 0; i < expectedHeaders.length; i++) {
      String actual = ExcelUtils.getString(headerRow, i);
      if (!expectedHeaders[i].equalsIgnoreCase(actual)) {
        throw new BaseException(
          HttpStatus.BAD_REQUEST,
          "EXCEL_INVALID_HEADER",
          "Column " + (i + 1) + " should be '" + expectedHeaders[i] + "' but found '" + actual + "'"
        );
      }
    }
  }
}
