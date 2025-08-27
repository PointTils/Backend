package com.pointtils.pointtils.src.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pointtils.pointtils.src.core.domain.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
}
