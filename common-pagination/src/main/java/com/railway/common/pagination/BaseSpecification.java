package com.railway.common.pagination;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Base helper for building JPA Specifications.
 *
 * Usage — in your entity's Specification class:
 *
 *   public class ZoneSpecification extends BaseSpecification<ZoneEntity> {
 *
 *       public static Specification<ZoneEntity> withFilters(FilterRequest request) {
 *           return (root, query, cb) -> {
 *               BaseSpecification<ZoneEntity> spec = new BaseSpecification<>();
 *               List<Predicate> predicates = new ArrayList<>();
 *
 *               // Active as of date (always apply)
 *               predicates.add(spec.activeAsOf(root, cb, request.getAsOfDate()));
 *
 *               // Entity-specific filters
 *               if (hasFilter(request, "name"))
 *                   predicates.add(spec.containsIgnoreCase(root, cb, "name", getFilter(request, "name")));
 *
 *               if (hasFilter(request, "code"))
 *                   predicates.add(spec.equalsIgnoreCase(root, cb, "code", getFilter(request, "code")));
 *
 *               return cb.and(predicates.toArray(new Predicate[0]));
 *           };
 *       }
 *   }
 */
public class BaseSpecification<T> {

  /**
   * Active as of a given date using effective_from / effective_till pattern.
   * If asOfDate is null, defaults to today.
   */
  public Predicate activeAsOf(Root<T> root, CriteriaBuilder cb, LocalDate asOfDate) {
    LocalDate date = asOfDate != null ? asOfDate : LocalDate.now();

    Predicate effectiveFromOk = cb.lessThanOrEqualTo(root.get("effectiveFrom"), date);
    Predicate effectiveTillNull = cb.isNull(root.get("effectiveTill"));
    Predicate effectiveTillAfter = cb.greaterThan(root.get("effectiveTill"), date);

    return cb.and(
      effectiveFromOk,
      cb.or(effectiveTillNull, effectiveTillAfter)
    );
  }

  /**
   * Case-insensitive LIKE filter — for name/description fields.
   */
  public Predicate containsIgnoreCase(Root<T> root, CriteriaBuilder cb,
                                      String field, String value) {
    Expression<String> lower = cb.lower(root.get(field));
    return cb.like(lower, "%" + value.toLowerCase() + "%");
  }

  /**
   * Case-insensitive exact match — for code/enum fields.
   */
  public Predicate equalsIgnoreCase(Root<T> root, CriteriaBuilder cb,
                                    String field, String value) {
    return cb.equal(cb.lower(root.get(field)), value.toLowerCase());
  }

  /**
   * Exact UUID match — for FK filters like zoneId, stateId etc.
   */
  public Predicate equalsUUID(Root<T> root, CriteriaBuilder cb,
                              String field, Object value) {
    return cb.equal(root.get(field), value);
  }

  // --- Utility helpers for reading filters safely ---

  public static boolean hasFilter(FilterRequest request, String key) {
    return request.getFilters() != null
      && request.getFilters().containsKey(key)
      && request.getFilters().get(key) != null
      && !request.getFilters().get(key).isBlank();
  }

  public static String getFilter(FilterRequest request, String key) {
    return request.getFilters().get(key).trim();
  }

  public static List<Predicate> newPredicateList() {
    return new ArrayList<>();
  }
}
