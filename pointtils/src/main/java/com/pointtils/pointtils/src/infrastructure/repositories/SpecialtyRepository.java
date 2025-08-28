package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.User;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {

    boolean existsByName(String name);
    List<User> findBySpecialties_Name(String name);
    
}
