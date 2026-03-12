package com.railway.common.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestBuilder {

  private PageRequestBuilder() {}

  public static Pageable from(FilterRequest request) {
    Sort sort = request.getSortDir().equalsIgnoreCase("asc")
      ? Sort.by(request.getSortBy()).ascending()
      : Sort.by(request.getSortBy()).descending();

    return PageRequest.of(request.getPage(), request.getSize(), sort);
  }
}
