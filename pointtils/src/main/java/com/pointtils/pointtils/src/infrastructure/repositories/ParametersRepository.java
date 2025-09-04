package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Parameters;

@Repository
public interface ParametersRepository extends JpaRepository<Parameters, Long> {
    
    Optional<Parameters> findByKey(String key);
    
    boolean existsByKey(String key);
}
