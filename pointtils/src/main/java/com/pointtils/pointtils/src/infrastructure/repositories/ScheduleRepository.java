package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import java.time.LocalTime;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID>, JpaSpecificationExecutor<Schedule> {
    List<Schedule> findByInterpreterIdAndDay(UUID interpreterId, DayOfWeek day);

    boolean existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(UUID interpreterId, DayOfWeek day, LocalTime endTime, LocalTime startTime);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.interpreter.id = :interpreterId AND s.day = :day AND s.id <> :id AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsConflictForUpdate(@Param("id") UUID id, @Param("interpreterId") UUID interpreterId, @Param("day") DayOfWeek day, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    default Page<Schedule> findAllWithFilters(Pageable pageable, UUID interpreterId, DayOfWeek day, LocalTime dateFrom, LocalTime dateTo) {
        Specification<Schedule> spec = ScheduleSpecifications.withFilters(interpreterId, day, dateFrom, dateTo);
        return findAll(spec, pageable);
    }
}
