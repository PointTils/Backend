package com.pointtils.pointtils.src.infrastructure.repositories;

import com.pointtils.pointtils.src.core.domain.entities.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, UUID> {

    List<UserApp> findAllByUserId(UUID userId);

    @Query("SELECT a FROM UserApp a WHERE (:userId IS NULL OR a.user.id = :userId) AND (:deviceId IS NULL OR a.deviceId = :deviceId)")
    List<UserApp> findAllByFilters(@Param("userId") UUID userId, @Param("deviceId") String deviceId);

}