package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class InterpreterSpecification {

    @SuppressWarnings("null")
    public static Specification<Interpreter> filter(
            InterpreterModality modality,
            String uf,
            String city,
            String neighborhood,
            List<UUID> specialties,
            Gender gender,
            DayOfWeek dayOfWeek,
            LocalTime requestedStart,
            LocalTime requestedEnd,
            String name
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Interpreter, Location> location = root.join("locations", JoinType.LEFT);
            Join<Interpreter, Schedule> schedule = root.join("schedules", JoinType.LEFT);

            Predicate predicate = cb.conjunction();

            if (modality == InterpreterModality.ALL) {
                predicate = cb.and(predicate, root.get("modality").in(InterpreterModality.ALL, InterpreterModality.ONLINE, InterpreterModality.PERSONALLY));
            } else if (modality != null) {
                predicate = cb.and(predicate, root.get("modality").in(InterpreterModality.ALL, modality));
            }
            if (name != null) predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            if (gender != null) predicate = cb.and(predicate, cb.equal(root.get("gender"), gender));
            if (uf != null) predicate = cb.and(predicate, cb.equal(location.get("uf"), uf));
            if (city != null) predicate = cb.and(predicate, cb.like(cb.lower(location.get("city")), "%" + city.toLowerCase() + "%"));
            if (neighborhood != null) predicate = cb.and(predicate, cb.like(cb.lower(location.get("neighborhood")), "%" + neighborhood.toLowerCase() + "%"));
            if (!Collections.isEmpty(specialties)) {
                Subquery<UUID> subquery = buildSpecialtiesSubquery(query, specialties, cb);
                predicate = cb.and(predicate, root.get("id").in(subquery));
            }
            if (dayOfWeek != null && requestedStart != null && requestedEnd != null) {
                predicate = cb.and(predicate, cb.equal(schedule.get("day"), dayOfWeek));
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(schedule.get("startTime"), requestedStart));
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(schedule.get("endTime"), requestedEnd));
            }

            return predicate;
        };
    }

    private static Subquery<UUID> buildSpecialtiesSubquery(CriteriaQuery<?> query, List<UUID> specialties,
                                                           CriteriaBuilder criteriaBuilder) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        var subRoot = subquery.from(UserSpecialty.class);
        subquery.select(subRoot.get("user").get("id"))
                .where(subRoot.get("specialty").get("id").in(specialties))
                .groupBy(subRoot.get("user").get("id"))
                .having(criteriaBuilder.equal(criteriaBuilder.countDistinct(subRoot.get("specialty").get("id")), specialties.size()));
        return subquery;
    }
}