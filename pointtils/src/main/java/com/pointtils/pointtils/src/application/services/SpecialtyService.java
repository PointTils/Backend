package com.pointtils.pointtils.src.application.services;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialtyService {
    
    private final SpecialtyRepository specialtyRepository;

    private static final String SPECIALTY_WITH_NAME = "Especialidade com nome ";
    private static final String ALREADY_EXIST = "já existe";

    
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }
    
    public Specialty getSpecialtyById(UUID id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Especialidade com id " + id + " não encontrada"));
    }
    
    public Specialty getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(SPECIALTY_WITH_NAME + name + " não encontrada"));
    }
    
    public List<Specialty> searchSpecialtiesByName(String name) {
        return specialtyRepository.findByNameContainingIgnoreCase(name);
    }
    
    public List<Specialty> getSpecialtiesByIds(List<UUID> ids) {
        return specialtyRepository.findByIds(ids);
    }
    
    public boolean specialtyExists(UUID id) {
        return specialtyRepository.existsById(id);
    }
    
    public boolean specialtyExistsByName(String name) {
        return specialtyRepository.existsByName(name);
    }
    
    public Specialty createSpecialty(String name) {
        if (specialtyRepository.existsByName(name)) {
            throw new IllegalArgumentException(SPECIALTY_WITH_NAME + name + ALREADY_EXIST);
        }
        
        Specialty specialty = new Specialty(name);
        return specialtyRepository.save(specialty);
    }
    
    public Specialty updateSpecialty(UUID id, String name) {
        Specialty specialty = getSpecialtyById(id);
        
        if (!specialty.getName().equals(name) && specialtyRepository.existsByName(name)) {
            throw new IllegalArgumentException(SPECIALTY_WITH_NAME + name + ALREADY_EXIST);
        }
        
        specialty.setName(name);
        return specialtyRepository.save(specialty);
    }
    
    public Specialty partialUpdateSpecialty(UUID id, String name) {
        Specialty specialty = getSpecialtyById(id);
        
        if (name != null) {
            if (!specialty.getName().equals(name) && specialtyRepository.existsByName(name)) {
                throw new IllegalArgumentException(SPECIALTY_WITH_NAME + name + ALREADY_EXIST);
            }
            specialty.setName(name);
        }
        
        return specialtyRepository.save(specialty);
    }
    
    public void deleteSpecialty(UUID id) {
        Specialty specialty = getSpecialtyById(id);
        specialtyRepository.delete(specialty);
    }
}
