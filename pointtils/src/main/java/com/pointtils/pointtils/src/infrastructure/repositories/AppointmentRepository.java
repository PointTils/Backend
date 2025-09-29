package com.pointtils.pointtils.src.infrastructure.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    
    List<Appointment> findByInterpreter(Interpreter interpreter);
    
    List<Appointment> findByUser(User user);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    List<Appointment> findByDate(LocalDate date);
    
    List<Appointment> findByInterpreterAndStatus(Interpreter interpreter, AppointmentStatus status);
}