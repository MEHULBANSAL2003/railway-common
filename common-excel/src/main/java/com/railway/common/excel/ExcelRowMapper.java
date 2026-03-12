package com.railway.common.excel;

import org.apache.poi.ss.usermodel.Row;

/**
 * Implement this per entity to define how a row maps to your DTO/entity.
 *
 * Example:
 *   @Component
 *   public class ZoneExcelRowMapper implements ExcelRowMapper<CreateZoneRequest> {
 *       @Override
 *       public CreateZoneRequest map(Row row, int rowNumber) {
 *           return CreateZoneRequest.builder()
 *               .name(ExcelUtils.getString(row, 0))
 *               .code(ExcelUtils.getString(row, 1))
 *               .reason(ExcelUtils.getString(row, 2))
 *               .build();
 *       }
 *
 *       @Override
 *       public String[] expectedHeaders() {
 *           return new String[]{"Name", "Code", "Reason"};
 *       }
 *   }
 */
public interface ExcelRowMapper<T> {

  /**
   * Map a single Excel row to your target type.
   * Throw IllegalArgumentException with a clear message if row is invalid.
   */
  T map(Row row, int rowNumber);

  /**
   * Expected headers in order — used for header validation.
   */
  String[] expectedHeaders();
}
