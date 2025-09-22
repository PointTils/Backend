package com.pointtils.pointtils.src.infrastructure.repositories;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

@Repository
public interface InterpreterRepository extends JpaRepository<Interpreter, UUID> {

    @Query("""
                  SELECT DISTINCT i
                  FROM Interpreter i
                  LEFT JOIN i.location l
                  LEFT JOIN i.specialties s
                  LEFT JOIN i.schedules sch
                  WHERE (:modality IS NULL OR i.modality = :modality)
                    AND (:gender IS NULL OR i.gender = :gender)
                    AND (:city IS NULL OR l.city = :city)
                    AND (:neighborhood IS NULL OR l.neighborhood = :neighborhood)
                    AND (:specialty IS NULL OR s.name IN :specialty)
                    AND (
                 :requestedDay IS NULL OR
                 (sch.day = :requestedDay AND
                  sch.startTime <= :requestedStartTime AND
                  sch.endTime >= :requestedEndTime)
            )
                  ORDER BY i.rating DESC
                  """)
    List<Interpreter> findAll(
            @Param("modality") InterpreterModality modality,
            @Param("gender") Gender gender,
            @Param("city") String city,
            @Param("neighborhood") String neighborhood,
            @Param("specialties") String specialty,
            @Param("requestedDay") DaysOfWeek requestedDay,
            @Param("requestedStart") LocalTime requestedStart,
            @Param("requestedEnd") LocalTime requestedEnd);

    List<Interpreter> findByModality(InterpreterModality modality);

    List<Interpreter> findByRatingGreaterThanEqual(BigDecimal rating);
}