package com.railway.common.excel;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExcelUploadResult {

  private final int totalRows;
  private final int successCount;
  private final int failedCount;
  private final List<RowError> errors;

  public boolean hasErrors() {
    return failedCount > 0;
  }

  @Getter
  @Builder
  public static class RowError {
    private final int rowNumber;
    private final String reason;
  }
}
