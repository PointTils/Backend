package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.InterpreterDocuments;

@Repository
public interface InterpreterDocumentsRepository extends JpaRepository<InterpreterDocuments, Long> {
    
    List<InterpreterDocuments> findByInterpreter(Interpreter interpreter);
    
    List<InterpreterDocuments> findByInterpreterIdOrderByIdAsc(UUID interpreterId);
}
