package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

@Repository
public interface InterpreterRepository extends JpaRepository<Interpreter, Long> {
    
    List<Interpreter> findByModality(InterpreterModality modality);

    List<Interpreter> findByRatingGreaterThanEqual(Double rating);
}