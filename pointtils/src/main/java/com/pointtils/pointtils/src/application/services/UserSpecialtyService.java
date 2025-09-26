package com.pointtils.pointtils.src.application.services;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserSpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSpecialtyService {
    
    private final UserSpecialtyRepository userSpecialtyRepository;
    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;
    
    public List<UserSpecialty> getUserSpecialties(UUID userId) {
        return userSpecialtyRepository.findByUserId(userId);
    }
    
    public UserSpecialty getUserSpecialty(UUID userId, UUID specialtyId) {
        return userSpecialtyRepository.findByUserIdAndSpecialtyId(userId, specialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade do usuário não encontrada"));
    }
    
    public boolean userHasSpecialty(UUID userId, UUID specialtyId) {
        return userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId);
    }
    
    public long countUserSpecialties(UUID userId) {
        return userSpecialtyRepository.countByUserId(userId);
    }
    
    @Transactional
    public List<UserSpecialty> addUserSpecialties(UUID userId, List<UUID> specialtyIds, boolean replaceExisting) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        List<Specialty> specialties = specialtyRepository.findByIds(specialtyIds);
        
        if (specialties.size() != specialtyIds.size()) {
            throw new IllegalArgumentException("Um ou mais IDs de especialidades são inválidos");
        }
        
        if (replaceExisting) {
            userSpecialtyRepository.deleteAllByUserId(userId);
        }
        
        List<UserSpecialty> userSpecialties = specialties.stream()
                .filter(specialty -> !userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialty.getId()))
                .map(specialty -> new UserSpecialty(specialty, user))
                .toList();
        
        return userSpecialtyRepository.saveAll(userSpecialties);
    }
    
    @Transactional
    public List<UserSpecialty> replaceUserSpecialties(UUID userId, List<UUID> specialtyIds) {
        return addUserSpecialties(userId, specialtyIds, true);
    }
    
    @Transactional
    public void removeUserSpecialty(UUID userId, UUID specialtyId) {
        if (!userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)) {
            throw new EntityNotFoundException("Usuário não possui esta especialidade");
        }
        
        userSpecialtyRepository.deleteByUserIdAndSpecialtyId(userId, specialtyId);
    }
    
    @Transactional
    public void removeUserSpecialties(UUID userId, List<UUID> specialtyIds) {
        List<UserSpecialty> existingSpecialties = userSpecialtyRepository.findByUserIdAndSpecialtyIds(userId, specialtyIds);
        
        if (existingSpecialties.size() != specialtyIds.size()) {
            throw new IllegalArgumentException("Uma ou mais especialidades não foram encontradas para este usuário");
        }
        
        userSpecialtyRepository.deleteByUserIdAndSpecialtyIds(userId, specialtyIds);
    }
    
    @Transactional
    public UserSpecialty updateUserSpecialty(UUID userSpecialtyId, UUID userId, UUID newSpecialtyId) {
        // Find the existing user specialty
        UserSpecialty userSpecialty = userSpecialtyRepository.findById(userSpecialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade do usuário com id " + userSpecialtyId + " não encontrada"));

        // Verify the user specialty belongs to the specified user
        if (!userSpecialty.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("A especialidade informada não pertence a este usuário");
        }
        
        // Check if the new specialty exists
        Specialty newSpecialty = specialtyRepository.findById(newSpecialtyId)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade com id " + newSpecialtyId + " não encontrada"));

        // Check if the user already has this new specialty
        if (userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, newSpecialtyId)) {
            throw new IllegalArgumentException("Usuário já possui esta especialidade");
        }
        
        // Update the specialty
        userSpecialty.setSpecialty(newSpecialty);
        
        return userSpecialtyRepository.save(userSpecialty);
    }
    
    @Transactional
    public void removeAllUserSpecialties(UUID userId) {
        userSpecialtyRepository.deleteAllByUserId(userId);
    }
}
