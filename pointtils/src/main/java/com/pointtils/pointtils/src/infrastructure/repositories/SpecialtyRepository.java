package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    Optional<Specialty> findByName(String name);

    List<Specialty> findByNameContainingIgnoreCase(String name);

    @Query("SELECT s FROM Specialty s WHERE s.id IN :ids")
    List<Specialty> findByIds(@Param("ids") List<UUID> ids);

    @Query("SELECT COUNT(s) > 0 FROM Specialty s WHERE s.id = :id")
    boolean existsById(@Param("id") UUID id);

    @Query("SELECT COUNT(s) > 0 FROM Specialty s WHERE s.name = :name")
    boolean existsByName(@Param("name") String name);
}