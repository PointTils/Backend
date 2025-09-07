package com.pointtils.pointtils.src.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;

@ExtendWith(MockitoExtension.class)
class SpecialtyServiceTest {

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private SpecialtyService specialtyService;

    private Specialty specialty;
    private UUID specialtyId;

    @BeforeEach
    void setUp() {
        specialtyId = UUID.randomUUID();
        specialty = new Specialty("Test Specialty");
        specialty.setId(specialtyId);
    }

    @Test
    void getAllSpecialties_ShouldReturnListOfSpecialties() {
        // Arrange
        when(specialtyRepository.findAll()).thenReturn(List.of(specialty));

        // Act
        List<Specialty> result = specialtyService.getAllSpecialties();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(specialty.getName(), result.get(0).getName());
        verify(specialtyRepository).findAll();
    }

    @Test
    void getSpecialtyById_WhenSpecialtyExists_ShouldReturnSpecialty() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));

        // Act
        Specialty result = specialtyService.getSpecialtyById(specialtyId);

        // Assert
        assertNotNull(result);
        assertEquals(specialty.getName(), result.getName());
        verify(specialtyRepository).findById(specialtyId);
    }

    @Test
    void getSpecialtyById_WhenSpecialtyNotExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.getSpecialtyById(specialtyId));
        verify(specialtyRepository).findById(specialtyId);
    }

    @Test
    void getSpecialtyByName_WhenSpecialtyExists_ShouldReturnSpecialty() {
        // Arrange
        when(specialtyRepository.findByName("Test Specialty")).thenReturn(Optional.of(specialty));

        // Act
        Specialty result = specialtyService.getSpecialtyByName("Test Specialty");

        // Assert
        assertNotNull(result);
        assertEquals(specialty.getName(), result.getName());
        verify(specialtyRepository).findByName("Test Specialty");
    }

    @Test
    void getSpecialtyByName_WhenSpecialtyNotExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.findByName("Non-existent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.getSpecialtyByName("Non-existent"));
        verify(specialtyRepository).findByName("Non-existent");
    }

    @Test
    void searchSpecialtiesByName_ShouldReturnMatchingSpecialties() {
        // Arrange
        when(specialtyRepository.findByNameContainingIgnoreCase("test")).thenReturn(List.of(specialty));

        // Act
        List<Specialty> result = specialtyService.searchSpecialtiesByName("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(specialty.getName(), result.get(0).getName());
        verify(specialtyRepository).findByNameContainingIgnoreCase("test");
    }

    @Test
    void getSpecialtiesByIds_ShouldReturnSpecialties() {
        // Arrange
        List<UUID> ids = List.of(specialtyId);
        when(specialtyRepository.findByIds(ids)).thenReturn(List.of(specialty));

        // Act
        List<Specialty> result = specialtyService.getSpecialtiesByIds(ids);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(specialty.getName(), result.get(0).getName());
        verify(specialtyRepository).findByIds(ids);
    }

    @Test
    void specialtyExists_WhenSpecialtyExists_ShouldReturnTrue() {
        // Arrange
        when(specialtyRepository.existsById(specialtyId)).thenReturn(true);

        // Act
        boolean result = specialtyService.specialtyExists(specialtyId);

        // Assert
        assertTrue(result);
        verify(specialtyRepository).existsById(specialtyId);
    }

    @Test
    void specialtyExistsByName_WhenSpecialtyExists_ShouldReturnTrue() {
        // Arrange
        when(specialtyRepository.existsByName("Test Specialty")).thenReturn(true);

        // Act
        boolean result = specialtyService.specialtyExistsByName("Test Specialty");

        // Assert
        assertTrue(result);
        verify(specialtyRepository).existsByName("Test Specialty");
    }

    @Test
    void createSpecialty_WhenNameNotExists_ShouldCreateSpecialty() {
        // Arrange
        when(specialtyRepository.existsByName("New Specialty")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> {
            Specialty s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        // Act
        Specialty result = specialtyService.createSpecialty("New Specialty");

        // Assert
        assertNotNull(result);
        assertEquals("New Specialty", result.getName());
        assertNotNull(result.getId());
        verify(specialtyRepository).existsByName("New Specialty");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void createSpecialty_WhenNameExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.existsByName("Existing Specialty")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.createSpecialty("Existing Specialty"));
        verify(specialtyRepository).existsByName("Existing Specialty");
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_WhenSpecialtyExistsAndNameNotTaken_ShouldUpdateSpecialty() {
        // Arrange
        Specialty existingSpecialty = new Specialty("Old Name");
        existingSpecialty.setId(specialtyId);
        
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.existsByName("New Name")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Specialty result = specialtyService.updateSpecialty(specialtyId, "New Name");

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).existsByName("New Name");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_WhenSpecialtyNotExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.updateSpecialty(specialtyId, "New Name"));
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).existsByName(anyString());
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void updateSpecialty_WhenNameTakenByDifferentSpecialty_ShouldThrowException() {
        // Arrange
        Specialty existingSpecialty = new Specialty("Old Name");
        existingSpecialty.setId(specialtyId);
        
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.existsByName("Taken Name")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.updateSpecialty(specialtyId, "Taken Name"));
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).existsByName("Taken Name");
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void deleteSpecialty_WhenSpecialtyExists_ShouldDeleteSpecialty() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(specialty));
        doNothing().when(specialtyRepository).delete(specialty);

        // Act
        specialtyService.deleteSpecialty(specialtyId);

        // Assert
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).delete(specialty);
    }

    @Test
    void deleteSpecialty_WhenSpecialtyNotExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.deleteSpecialty(specialtyId));
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).delete(any(Specialty.class));
    }

    @Test
    void partialUpdateSpecialty_WhenNameProvidedAndNotTaken_ShouldUpdateSpecialty() {
        // Arrange
        Specialty existingSpecialty = new Specialty("Old Name");
        existingSpecialty.setId(specialtyId);
        
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.existsByName("New Name")).thenReturn(false);
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Specialty result = specialtyService.partialUpdateSpecialty(specialtyId, "New Name");

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).existsByName("New Name");
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void partialUpdateSpecialty_WhenNameIsNull_ShouldNotUpdateName() {
        // Arrange
        Specialty existingSpecialty = new Specialty("Old Name");
        existingSpecialty.setId(specialtyId);
        
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Specialty result = specialtyService.partialUpdateSpecialty(specialtyId, null);

        // Assert
        assertNotNull(result);
        assertEquals("Old Name", result.getName()); // Name should remain unchanged
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).existsByName(anyString());
        verify(specialtyRepository).save(any(Specialty.class));
    }

    @Test
    void partialUpdateSpecialty_WhenNameTakenByDifferentSpecialty_ShouldThrowException() {
        // Arrange
        Specialty existingSpecialty = new Specialty("Old Name");
        existingSpecialty.setId(specialtyId);
        
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.of(existingSpecialty));
        when(specialtyRepository.existsByName("Taken Name")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.partialUpdateSpecialty(specialtyId, "Taken Name"));
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository).existsByName("Taken Name");
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }

    @Test
    void partialUpdateSpecialty_WhenSpecialtyNotExists_ShouldThrowException() {
        // Arrange
        when(specialtyRepository.findById(specialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> specialtyService.partialUpdateSpecialty(specialtyId, "New Name"));
        verify(specialtyRepository).findById(specialtyId);
        verify(specialtyRepository, never()).existsByName(anyString());
        verify(specialtyRepository, never()).save(any(Specialty.class));
    }
}
