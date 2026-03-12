package com.railway.common.pagination;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FilterRequest {

  @Min(value = 0, message = "Page must be >= 0")
  private int page = 0;

  @Min(value = 1, message = "Size must be >= 1")
  @Max(value = 100, message = "Size must be <= 100")
  private int size = 20;

  private String sortBy = "createdAt";
  private String sortDir = "desc";       // asc | desc

  // Generic key-value filters — each entity's Specification reads what it needs
  private Map<String, String> filters = new HashMap<>();

  // Common date range filter (effective date aware)
  private LocalDate asOfDate;             // null = today (what's active right now)
}
