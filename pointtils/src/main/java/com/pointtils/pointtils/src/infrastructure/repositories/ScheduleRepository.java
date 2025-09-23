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

public interface ScheduleRepository extends JpaRepository<Schedule, UUID>, JpaSpecificationExecutor<Schedule> {
    List<Schedule> findByInterpreterIdAndDay(UUID interpreterId, DayOfWeek day);

    boolean existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(UUID interpreterId, DayOfWeek day, LocalTime endTime, LocalTime startTime);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.interpreterId = :interpreterId AND s.day = :day AND s.id <> :id AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsConflictForUpdate(@Param("id") UUID id, @Param("interpreterId") UUID interpreterId, @Param("day") DayOfWeek day, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Query(value = "SELECT * FROM schedule WHERE (:interpreterId IS NULL OR interpreter_id = :interpreterId) " +
            "AND (CASE WHEN :day IS NOT NULL THEN day = CAST(:day AS schedule_day_enum) ELSE true END) " +
            "AND (CASE WHEN :dateFrom IS NOT NULL THEN start_time >= CAST(:dateFrom AS time) ELSE true END) " +
            "AND (CASE WHEN :dateTo IS NOT NULL THEN end_time <= CAST(:dateTo AS time) ELSE true END)", nativeQuery = true)
    Page<Schedule> findAllWithFilters(
        Pageable pageable,
        @Param("interpreterId") UUID interpreterId,
        @Param("day") String day,
        @Param("dateFrom") String dateFrom,
        @Param("dateTo") String dateTo
    );
}
