package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    List<Rating> findByAppointment(Appointment appointment);
    
    List<Rating> findByAppointmentInterpreterIdOrderByStarsDesc(UUID interpreterId);
}
