package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, UUID> {

    boolean existsByCnpj(String cnpj);

    List<Enterprise> findAllByStatus(UserStatus status);

    Optional<Enterprise> findByIdAndStatus(UUID id, UserStatus status);
}
