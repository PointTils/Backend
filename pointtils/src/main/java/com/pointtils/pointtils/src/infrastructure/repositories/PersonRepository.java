package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {
    
    Optional<Person> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
