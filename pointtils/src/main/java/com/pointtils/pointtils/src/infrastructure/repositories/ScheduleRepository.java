package com.pointtils.pointtils.src.infrastructure.repositories;

import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.infrastructure.repositories.spec.ScheduleSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepository extends JpaRepository<Schedule, UUID>, JpaSpecificationExecutor<Schedule> {
    List<Schedule> findByInterpreterIdAndDay(UUID interpreterId, DayOfWeek day);

    boolean existsByInterpreterIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(UUID interpreterId, DayOfWeek day, LocalTime endTime, LocalTime startTime);

    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE s.interpreter.id = :interpreterId AND s.day = :day AND s.id <> :id AND s.startTime < :endTime AND s.endTime > :startTime")
    boolean existsConflictForUpdate(@Param("id") UUID id, @Param("interpreterId") UUID interpreterId, @Param("day") DayOfWeek day, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    default Page<Schedule> findAllWithFilters(Pageable pageable, UUID interpreterId, DayOfWeek day, LocalTime dateFrom, LocalTime dateTo) {
        Specification<Schedule> spec = ScheduleSpecifications.withFilters(interpreterId, day, dateFrom, dateTo);
        return findAll(spec, pageable);
    }

    @Query(value = """
            WITH date_range AS (
                SELECT
                    i AS selected_date,
                    EXTRACT(DOW FROM i)::int AS dow
                FROM generate_series(CAST(:dateFrom AS DATE), CAST(:dateTo AS DATE), '1 day'::interval) AS i
            ),
            schedule_days AS (
                SELECT 
                    s.id AS schedule_id,
                    s.interpreter_id,
                    s.day,
                    s.start_time,
                    s.end_time,
                    n.selected_date
                FROM schedule s
                JOIN date_range n ON 
                    (
                        CASE s.day
                          WHEN 'SUN' THEN 0
                          WHEN 'MON' THEN 1
                          WHEN 'TUE' THEN 2
                          WHEN 'WED' THEN 3
                          WHEN 'THU' THEN 4
                          WHEN 'FRI' THEN 5
                          WHEN 'SAT' THEN 6
                        END
                    ) = n.dow
                WHERE interpreter_id = :interpreterId
            ),
            time_slots AS (
                -- Slots iniciando em hora cheia
                SELECT
                    sd.schedule_id,
                    sd.interpreter_id,
                    sd.day,
                    sd.selected_date,
                    generate_series(
                        '2000-01-01'::timestamp + sd.start_time,
                        '2000-01-01'::timestamp + sd.end_time - interval '1 hour',
                        interval '1 hour'
                    ) AS slot_start
                FROM schedule_days sd

                UNION ALL

                -- Slots iniciando em meia hora
                SELECT
                    sd.schedule_id,
                    sd.interpreter_id,
                    sd.day,
                    sd.selected_date,
                    generate_series(
                        '2000-01-01'::timestamp + sd.start_time + interval '30 minutes',
                        '2000-01-01'::timestamp + sd.end_time - interval '30 minutes' - interval '1 hour',
                        interval '1 hour'
                    ) AS slot_start
                FROM schedule_days sd
            ),
            slots_with_end AS (
                SELECT
                    *,
                    slot_start + interval '1 hour' AS slot_end
                FROM time_slots
            ),
            filtered_slots AS (
                SELECT sw.*
                FROM slots_with_end sw
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM appointment a
                    WHERE a.interpreter_id = sw.interpreter_id
                      AND a.date = sw.selected_date
                      AND ('2000-01-01'::timestamp + a.start_time) < sw.slot_end
                      AND ('2000-01-01'::timestamp + a.end_time) > sw.slot_start
                )
            )
            SELECT
                interpreter_id AS interpreterId,
                selected_date::date,
                slot_start::time AS startTime,
                slot_end::time AS endTime
            FROM filtered_slots
            ORDER BY interpreter_id, selected_date, slot_start
            """, nativeQuery = true)
    List<Object[]> findAvailableTimeSlots(@Param("interpreterId") UUID interpreterId,
                                          @Param("dateFrom") LocalDate dateFrom,
                                          @Param("dateTo") LocalDate dateTo);
}
