package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
  
}
