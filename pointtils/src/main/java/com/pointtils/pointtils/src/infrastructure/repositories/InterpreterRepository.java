package com.pointtils.pointtils.src.infrastructure.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

@Repository
public interface InterpreterRepository extends JpaRepository<Interpreter, UUID>, JpaSpecificationExecutor<Interpreter> {

    List<Interpreter> findByModality(InterpreterModality modality);

    List<Interpreter> findByRatingGreaterThanEqual(BigDecimal rating);
}