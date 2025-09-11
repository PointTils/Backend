package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Enterprise;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {

    Optional<Enterprise> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
