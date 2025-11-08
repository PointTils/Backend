package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterSpecificationFilterDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class InterpreterSpecification {

    private static final String START_TIME_FIELD = "startTime";
    private static final String END_TIME_FIELD = "endTime";

    @SuppressWarnings("null")
    public static Specification<Interpreter> filter(
            InterpreterSpecificationFilterDTO dto
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            Predicate predicate = cb.conjunction();

            if (dto.getName() != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + dto.getName().toLowerCase() + "%"));
            }
            if (dto.getGender() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("gender"), dto.getGender()));
            }
            predicate = checkIfInterpreterHasModality(predicate, cb, root, dto.getModality());
            predicate = checkIfInterpreterHasLocation(predicate, cb, root, dto.getUf(), dto.getCity(), dto.getNeighborhood());
            predicate = checkIfInterpreterHasSpecialties(predicate, query, cb, root, dto.getSpecialties());
            predicate = checkIfInterpreterIsAvailableOnRequestedDate(predicate, query, cb, root, dto.getAvailableDate());
            return predicate;
        };
    }

    private static Predicate checkIfInterpreterHasModality(Predicate predicate,
                                                           CriteriaBuilder criteriaBuilder,
                                                           Root<Interpreter> root,
                                                           InterpreterModality modality) {
        if (modality == InterpreterModality.ALL) {
            predicate = criteriaBuilder.and(predicate, root.get("modality")
                    .in(InterpreterModality.ALL, InterpreterModality.ONLINE, InterpreterModality.PERSONALLY));
        } else if (Objects.nonNull(modality)) {
            predicate = criteriaBuilder.and(predicate, root.get("modality").in(InterpreterModality.ALL, modality));
        }
        return predicate;
    }

    private static Predicate checkIfInterpreterHasLocation(Predicate predicate,
                                                           CriteriaBuilder criteriaBuilder,
                                                           Root<Interpreter> root,
                                                           String uf,
                                                           String city,
                                                           String neighborhood) {

        Join<Interpreter, Location> location = root.join("locations", JoinType.LEFT);
        if (Objects.nonNull(uf)) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(location.get("uf"), uf));
        }
        if (Objects.nonNull(city)) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder
                    .like(criteriaBuilder.lower(location.get("city")), "%" + city.toLowerCase() + "%"));
        }
        if (Objects.nonNull(neighborhood)) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder
                    .like(criteriaBuilder.lower(location.get("neighborhood")), "%" + neighborhood.toLowerCase() + "%"));
        }
        return predicate;
    }

    private static Predicate checkIfInterpreterHasSpecialties(Predicate predicate,
                                                              CriteriaQuery<?> query,
                                                              CriteriaBuilder criteriaBuilder,
                                                              Root<Interpreter> root,
                                                              List<UUID> specialties) {
        if (Collections.isEmpty(specialties)) {
            return predicate;
        }
        Subquery<UUID> subquery = query.subquery(UUID.class);
        var subRoot = subquery.from(UserSpecialty.class);
        subquery.select(subRoot.get("user").get("id"))
                .where(subRoot.get("specialty").get("id").in(specialties))
                .groupBy(subRoot.get("user").get("id"))
                .having(criteriaBuilder.equal(criteriaBuilder.countDistinct(subRoot.get("specialty").get("id")), specialties.size()));
        return criteriaBuilder.and(predicate, root.get("id").in(subquery));
    }

    private static Predicate checkIfInterpreterIsAvailableOnRequestedDate(Predicate predicate,
                                                                          CriteriaQuery<?> query,
                                                                          CriteriaBuilder criteriaBuilder,
                                                                          Root<Interpreter> root,
                                                                          LocalDateTime availableDate) {
        if (Objects.isNull(availableDate)) {
            return predicate;
        }

        LocalDate requestedDate = availableDate.toLocalDate();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(availableDate.getDayOfWeek().name().substring(0, 3));
        LocalTime requestedStart = availableDate.toLocalTime();
        LocalTime requestedEnd = requestedStart.plusHours(1);

        predicate = checkIfInterpreterHasScheduleOnRequestedDate(predicate, criteriaBuilder, root, dayOfWeek, requestedStart, requestedEnd);
        return checkIfInterpreterHasNoAppointmentOnRequestDate(predicate, query, criteriaBuilder, root, requestedDate, requestedStart, requestedEnd);
    }

    private static Predicate checkIfInterpreterHasScheduleOnRequestedDate(Predicate predicate,
                                                                          CriteriaBuilder criteriaBuilder,
                                                                          Root<Interpreter> root,
                                                                          DayOfWeek dayOfWeek,
                                                                          LocalTime requestedStart,
                                                                          LocalTime requestedEnd) {
        Join<Interpreter, Schedule> schedule = root.join("schedules", JoinType.LEFT);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(schedule.get("day"), dayOfWeek));
        predicate = criteriaBuilder.and(predicate, criteriaBuilder
                .lessThanOrEqualTo(schedule.get(START_TIME_FIELD), requestedStart));
        return criteriaBuilder.and(predicate, criteriaBuilder
                .greaterThanOrEqualTo(schedule.get(END_TIME_FIELD), requestedEnd));
    }

    private static Predicate checkIfInterpreterHasNoAppointmentOnRequestDate(Predicate predicate,
                                                                             CriteriaQuery<?> query,
                                                                             CriteriaBuilder criteriaBuilder,
                                                                             Root<Interpreter> root,
                                                                             LocalDate requestedDate,
                                                                             LocalTime requestedStart,
                                                                             LocalTime requestedEnd) {
        Subquery<UUID> subquery = query.subquery(UUID.class);
        var subRoot = subquery.from(Appointment.class);
        subquery.select(subRoot.get("interpreter").get("id"))
                .where(criteriaBuilder.and(
                        subRoot.get("status").in(AppointmentStatus.ACCEPTED, AppointmentStatus.COMPLETED),
                        criteriaBuilder.equal(subRoot.get("date"), requestedDate),
                        criteriaBuilder.or(
                                criteriaBuilder.and(
                                        criteriaBuilder.greaterThan(subRoot.get(START_TIME_FIELD), requestedStart),
                                        criteriaBuilder.lessThan(subRoot.get(START_TIME_FIELD), requestedEnd)
                                ),
                                criteriaBuilder.and(
                                        criteriaBuilder.greaterThan(subRoot.get(END_TIME_FIELD), requestedStart),
                                        criteriaBuilder.lessThan(subRoot.get(END_TIME_FIELD), requestedEnd)
                                ),
                                criteriaBuilder.and(
                                        criteriaBuilder.lessThanOrEqualTo(subRoot.get(START_TIME_FIELD), requestedStart),
                                        criteriaBuilder.greaterThanOrEqualTo(subRoot.get(END_TIME_FIELD), requestedEnd)
                                )
                        )
                ));
        return criteriaBuilder.and(predicate, criteriaBuilder.not(root.get("id").in(subquery)));
    }
}