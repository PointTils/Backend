package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.WeekDay;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByInterpreter(Interpreter interpreter);
    
    List<Schedule> findByDay(WeekDay day);
    
    List<Schedule> findByInterpreterAndDay(Interpreter interpreter, WeekDay day);
}
