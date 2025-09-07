package com.pointtils.pointtils.src.infrastructure.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;

@Repository
public interface UserSpecialtyRepository extends JpaRepository<UserSpecialty, UUID> {
    
    List<UserSpecialty> findByUser(User user);
    
    List<UserSpecialty> findByUserId(UUID userId);
    
    @Query("SELECT us FROM UserSpecialty us WHERE us.user.id = :userId AND us.specialty.id = :specialtyId")
    Optional<UserSpecialty> findByUserIdAndSpecialtyId(@Param("userId") UUID userId, @Param("specialtyId") UUID specialtyId);
    
    @Query("SELECT COUNT(us) > 0 FROM UserSpecialty us WHERE us.user.id = :userId AND us.specialty.id = :specialtyId")
    boolean existsByUserIdAndSpecialtyId(@Param("userId") UUID userId, @Param("specialtyId") UUID specialtyId);
    
    @Query("SELECT COUNT(us) FROM UserSpecialty us WHERE us.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT us FROM UserSpecialty us WHERE us.user.id = :userId AND us.specialty.id IN :specialtyIds")
    List<UserSpecialty> findByUserIdAndSpecialtyIds(@Param("userId") UUID userId, @Param("specialtyIds") List<UUID> specialtyIds);
    
    @Query("DELETE FROM UserSpecialty us WHERE us.user.id = :userId AND us.specialty.id = :specialtyId")
    void deleteByUserIdAndSpecialtyId(@Param("userId") UUID userId, @Param("specialtyId") UUID specialtyId);
    
    @Query("DELETE FROM UserSpecialty us WHERE us.user.id = :userId AND us.specialty.id IN :specialtyIds")
    void deleteByUserIdAndSpecialtyIds(@Param("userId") UUID userId, @Param("specialtyIds") List<UUID> specialtyIds);
    
    @Query("DELETE FROM UserSpecialty us WHERE us.user.id = :userId")
    void deleteAllByUserId(@Param("userId") UUID userId);
}
