package com.pointtils.pointtils.src.infrastructure.repositories;

import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScheduleSpecifications {

    public static Specification<Schedule> withFilters(UUID interpreterId, DayOfWeek day, LocalTime dateFrom, LocalTime dateTo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (interpreterId != null) {
                predicates.add(criteriaBuilder.equal(root.get("interpreter").get("id"), interpreterId));
            }

            if (day != null) {
                predicates.add(criteriaBuilder.equal(root.get("day"), day));
            }

            if (dateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), dateFrom));
            }

            if (dateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), dateTo));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}