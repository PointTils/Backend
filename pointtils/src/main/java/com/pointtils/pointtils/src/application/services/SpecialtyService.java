package com.pointtils.pointtils.src.application.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SpecialtyService {
    
    private final SpecialtyRepository specialtyRepository;
    
    public List<Specialty> getAllSpecialties() {
        return specialtyRepository.findAll();
    }
    
    public Specialty getSpecialtyById(UUID id) {
        return specialtyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialty not found with id: " + id));
    }
    
    public Specialty getSpecialtyByName(String name) {
        return specialtyRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Specialty not found with name: " + name));
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
            throw new RuntimeException("Specialty with name '" + name + "' already exists");
        }
        
        Specialty specialty = new Specialty(UUID.randomUUID(), name);
        return specialtyRepository.save(specialty);
    }
    
    public Specialty updateSpecialty(UUID id, String name) {
        Specialty specialty = getSpecialtyById(id);
        
        if (!specialty.getName().equals(name) && specialtyRepository.existsByName(name)) {
            throw new RuntimeException("Specialty with name '" + name + "' already exists");
        }
        
        specialty.setName(name);
        return specialtyRepository.save(specialty);
    }
    
    public Specialty partialUpdateSpecialty(UUID id, String name) {
        Specialty specialty = getSpecialtyById(id);
        
        if (name != null) {
            if (!specialty.getName().equals(name) && specialtyRepository.existsByName(name)) {
                throw new RuntimeException("Specialty with name '" + name + "' already exists");
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
