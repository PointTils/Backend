package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class InterpreterSpecification {

    @SuppressWarnings("null")
    public static Specification<Interpreter> filter(
            InterpreterModality modality,
            String uf,
            String city,
            String neighborhood,
            String specialty,
            Gender gender,
            DaysOfWeek dayOfWeek,
            LocalTime requestedStart,
            LocalTime requestedEnd
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Interpreter, Location> location = root.join("location", JoinType.LEFT);
            Join<Interpreter, Specialty> spec = root.join("specialties", JoinType.LEFT);
            Join<Interpreter, Schedule> schedule = root.join("schedules", JoinType.LEFT);

            Predicate predicate = cb.conjunction();

            if (modality != null) predicate = cb.and(predicate, cb.equal(root.get("modality"), modality));
            if (gender != null) predicate = cb.and(predicate, cb.equal(root.get("gender"), gender));
            if (uf != null) predicate = cb.and(predicate, cb.equal(location.get("uf"), uf));
            if (city != null) predicate = cb.and(predicate, cb.like(cb.lower(location.get("city")), "%" + city.toLowerCase() + "%"));
            if (neighborhood != null) predicate = cb.and(predicate, cb.like(cb.lower(location.get("neighborhood")), "%" + neighborhood.toLowerCase() + "%"));
            if (specialty != null) predicate = cb.and(predicate, cb.equal(cb.lower(spec.get("name")), specialty.toLowerCase()));
            if (dayOfWeek != null && requestedStart != null && requestedEnd != null) {
                predicate = cb.and(predicate, cb.equal(schedule.get("day"), dayOfWeek));
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(schedule.get("startTime"), requestedStart));
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(schedule.get("endTime"), requestedEnd));
            }

            return predicate;
        };
    }
}