package com.pointtils.pointtils.src.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pointtils.pointtils.src.core.domain.entities.Specialty;
import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.core.domain.entities.UserSpecialty;
import com.pointtils.pointtils.src.infrastructure.repositories.SpecialtyRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.UserSpecialtyRepository;

@ExtendWith(MockitoExtension.class)
class UserSpecialtyServiceTest {

    @Mock
    private UserSpecialtyRepository userSpecialtyRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSpecialtyService userSpecialtyService;

    private User user;
    private Specialty specialty;
    private UserSpecialty userSpecialty;
    private UUID userId;
    private UUID specialtyId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();
        
        user = new User() {
            @Override
            public String getDisplayName() {
                return "Test User";
            }

            @Override
            public String getType() {
                return "CLIENT";
            }
        };
        user.setId(userId);
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setPhone("11999999999");
        user.setStatus(UserStatus.ACTIVE);

        specialty = new Specialty("Test Specialty");
        specialty.setId(specialtyId);

        userSpecialty = new UserSpecialty(specialty, user);
        userSpecialty.setId(UUID.randomUUID());
    }

    @Test
    void getUserSpecialties_ShouldReturnListOfUserSpecialties() {
        // Arrange
        when(userSpecialtyRepository.findByUserId(userId)).thenReturn(List.of(userSpecialty));

        // Act
        List<UserSpecialty> result = userSpecialtyService.getUserSpecialties(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userSpecialty.getId(), result.get(0).getId());
        verify(userSpecialtyRepository).findByUserId(userId);
    }

    @Test
    void getUserSpecialty_WhenExists_ShouldReturnUserSpecialty() {
        // Arrange
        when(userSpecialtyRepository.findByUserIdAndSpecialtyId(userId, specialtyId))
            .thenReturn(Optional.of(userSpecialty));

        // Act
        UserSpecialty result = userSpecialtyService.getUserSpecialty(userId, specialtyId);

        // Assert
        assertNotNull(result);
        assertEquals(userSpecialty.getId(), result.getId());
        verify(userSpecialtyRepository).findByUserIdAndSpecialtyId(userId, specialtyId);
    }

    @Test
    void getUserSpecialty_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(userSpecialtyRepository.findByUserIdAndSpecialtyId(userId, specialtyId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userSpecialtyService.getUserSpecialty(userId, specialtyId));
        verify(userSpecialtyRepository).findByUserIdAndSpecialtyId(userId, specialtyId);
    }

    @Test
    void userHasSpecialty_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(true);

        // Act
        boolean result = userSpecialtyService.userHasSpecialty(userId, specialtyId);

        // Assert
        assertTrue(result);
        verify(userSpecialtyRepository).existsByUserIdAndSpecialtyId(userId, specialtyId);
    }

    @Test
    void countUserSpecialties_ShouldReturnCount() {
        // Arrange
        when(userSpecialtyRepository.countByUserId(userId)).thenReturn(5L);

        // Act
        long result = userSpecialtyService.countUserSpecialties(userId);

        // Assert
        assertEquals(5L, result);
        verify(userSpecialtyRepository).countByUserId(userId);
    }

    @Test
    void addUserSpecialties_WhenValidIdsAndNotReplace_ShouldAddSpecialties() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId);
        List<Specialty> specialties = List.of(specialty);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(specialtyRepository.findByIds(specialtyIds)).thenReturn(specialties);
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(false);
        when(userSpecialtyRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<UserSpecialty> result = userSpecialtyService.addUserSpecialties(userId, specialtyIds, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(specialtyRepository).findByIds(specialtyIds);
        verify(userSpecialtyRepository, never()).deleteAllByUserId(userId);
        verify(userSpecialtyRepository).saveAll(any());
    }

    @Test
    void addUserSpecialties_WhenValidIdsAndReplace_ShouldReplaceSpecialties() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId);
        List<Specialty> specialties = List.of(specialty);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(specialtyRepository.findByIds(specialtyIds)).thenReturn(specialties);
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(false);
        when(userSpecialtyRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(userSpecialtyRepository).deleteAllByUserId(userId);

        // Act
        List<UserSpecialty> result = userSpecialtyService.addUserSpecialties(userId, specialtyIds, true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findById(userId);
        verify(specialtyRepository).findByIds(specialtyIds);
        verify(userSpecialtyRepository).deleteAllByUserId(userId);
        verify(userSpecialtyRepository).saveAll(any());
    }

    @Test
    void addUserSpecialties_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.addUserSpecialties(userId, specialtyIds, false));
        verify(userRepository).findById(userId);
        verify(specialtyRepository, never()).findByIds(any());
    }

    @Test
    void addUserSpecialties_WhenInvalidSpecialtyIds_ShouldThrowException() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId, UUID.randomUUID());
        List<Specialty> specialties = List.of(specialty);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(specialtyRepository.findByIds(specialtyIds)).thenReturn(specialties);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.addUserSpecialties(userId, specialtyIds, false));
        verify(userRepository).findById(userId);
        verify(specialtyRepository).findByIds(specialtyIds);
        verify(userSpecialtyRepository, never()).saveAll(any());
    }

    @Test
    void replaceUserSpecialties_ShouldCallAddWithReplaceTrue() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId);
        List<Specialty> specialties = List.of(specialty);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(specialtyRepository.findByIds(specialtyIds)).thenReturn(specialties);
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(false);
        when(userSpecialtyRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(userSpecialtyRepository).deleteAllByUserId(userId);

        // Act
        List<UserSpecialty> result = userSpecialtyService.replaceUserSpecialties(userId, specialtyIds);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userSpecialtyRepository).deleteAllByUserId(userId);
        verify(userSpecialtyRepository).saveAll(any());
    }

    @Test
    void removeUserSpecialty_WhenExists_ShouldRemoveSpecialty() {
        // Arrange
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(true);
        doNothing().when(userSpecialtyRepository).deleteByUserIdAndSpecialtyId(userId, specialtyId);

        // Act
        userSpecialtyService.removeUserSpecialty(userId, specialtyId);

        // Assert
        verify(userSpecialtyRepository).existsByUserIdAndSpecialtyId(userId, specialtyId);
        verify(userSpecialtyRepository).deleteByUserIdAndSpecialtyId(userId, specialtyId);
    }

    @Test
    void removeUserSpecialty_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, specialtyId)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.removeUserSpecialty(userId, specialtyId));
        verify(userSpecialtyRepository).existsByUserIdAndSpecialtyId(userId, specialtyId);
        verify(userSpecialtyRepository, never()).deleteByUserIdAndSpecialtyId(any(), any());
    }

    @Test
    void removeUserSpecialties_WhenAllExist_ShouldRemoveSpecialties() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId);
        when(userSpecialtyRepository.findByUserIdAndSpecialtyIds(userId, specialtyIds))
            .thenReturn(List.of(userSpecialty));
        doNothing().when(userSpecialtyRepository).deleteByUserIdAndSpecialtyIds(userId, specialtyIds);

        // Act
        userSpecialtyService.removeUserSpecialties(userId, specialtyIds);

        // Assert
        verify(userSpecialtyRepository).findByUserIdAndSpecialtyIds(userId, specialtyIds);
        verify(userSpecialtyRepository).deleteByUserIdAndSpecialtyIds(userId, specialtyIds);
    }

    @Test
    void removeUserSpecialties_WhenSomeNotExist_ShouldThrowException() {
        // Arrange
        List<UUID> specialtyIds = List.of(specialtyId, UUID.randomUUID());
        when(userSpecialtyRepository.findByUserIdAndSpecialtyIds(userId, specialtyIds))
            .thenReturn(List.of(userSpecialty));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.removeUserSpecialties(userId, specialtyIds));
        verify(userSpecialtyRepository).findByUserIdAndSpecialtyIds(userId, specialtyIds);
        verify(userSpecialtyRepository, never()).deleteByUserIdAndSpecialtyIds(any(), any());
    }

    @Test
    void updateUserSpecialty_WhenValid_ShouldUpdateSpecialty() {
        // Arrange
        UUID userSpecialtyId = UUID.randomUUID();
        UUID newSpecialtyId = UUID.randomUUID();
        Specialty newSpecialty = new Specialty("New Specialty");
        newSpecialty.setId(newSpecialtyId);

        when(userSpecialtyRepository.findById(userSpecialtyId)).thenReturn(Optional.of(userSpecialty));
        when(specialtyRepository.findById(newSpecialtyId)).thenReturn(Optional.of(newSpecialty));
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, newSpecialtyId)).thenReturn(false);
        when(userSpecialtyRepository.save(any(UserSpecialty.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserSpecialty result = userSpecialtyService.updateUserSpecialty(userSpecialtyId, userId, newSpecialtyId);

        // Assert
        assertNotNull(result);
        assertEquals(newSpecialtyId, result.getSpecialty().getId());
        verify(userSpecialtyRepository).findById(userSpecialtyId);
        verify(specialtyRepository).findById(newSpecialtyId);
        verify(userSpecialtyRepository).existsByUserIdAndSpecialtyId(userId, newSpecialtyId);
        verify(userSpecialtyRepository).save(any(UserSpecialty.class));
    }

    @Test
    void updateUserSpecialty_WhenUserSpecialtyNotFound_ShouldThrowException() {
        // Arrange
        UUID userSpecialtyId = UUID.randomUUID();
        UUID newSpecialtyId = UUID.randomUUID();

        when(userSpecialtyRepository.findById(userSpecialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.updateUserSpecialty(userSpecialtyId, userId, newSpecialtyId));
        verify(userSpecialtyRepository).findById(userSpecialtyId);
        verify(specialtyRepository, never()).findById(any());
    }

    @Test
    void updateUserSpecialty_WhenWrongUser_ShouldThrowException() {
        // Arrange
        UUID userSpecialtyId = UUID.randomUUID();
        UUID newSpecialtyId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();

        when(userSpecialtyRepository.findById(userSpecialtyId)).thenReturn(Optional.of(userSpecialty));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.updateUserSpecialty(userSpecialtyId, differentUserId, newSpecialtyId));
        verify(userSpecialtyRepository).findById(userSpecialtyId);
        verify(specialtyRepository, never()).findById(any());
    }

    @Test
    void updateUserSpecialty_WhenNewSpecialtyNotFound_ShouldThrowException() {
        // Arrange
        UUID userSpecialtyId = UUID.randomUUID();
        UUID newSpecialtyId = UUID.randomUUID();

        when(userSpecialtyRepository.findById(userSpecialtyId)).thenReturn(Optional.of(userSpecialty));
        when(specialtyRepository.findById(newSpecialtyId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.updateUserSpecialty(userSpecialtyId, userId, newSpecialtyId));
        verify(userSpecialtyRepository).findById(userSpecialtyId);
        verify(specialtyRepository).findById(newSpecialtyId);
    }

    @Test
    void updateUserSpecialty_WhenUserAlreadyHasNewSpecialty_ShouldThrowException() {
        // Arrange
        UUID userSpecialtyId = UUID.randomUUID();
        UUID newSpecialtyId = UUID.randomUUID();
        Specialty newSpecialty = new Specialty("New Specialty");
        specialty.setId(newSpecialtyId);

        when(userSpecialtyRepository.findById(userSpecialtyId)).thenReturn(Optional.of(userSpecialty));
        when(specialtyRepository.findById(newSpecialtyId)).thenReturn(Optional.of(newSpecialty));
        when(userSpecialtyRepository.existsByUserIdAndSpecialtyId(userId, newSpecialtyId)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            userSpecialtyService.updateUserSpecialty(userSpecialtyId, userId, newSpecialtyId));
        verify(userSpecialtyRepository).findById(userSpecialtyId);
        verify(specialtyRepository).findById(newSpecialtyId);
        verify(userSpecialtyRepository).existsByUserIdAndSpecialtyId(userId, newSpecialtyId);
        verify(userSpecialtyRepository, never()).save(any());
    }

    @Test
    void removeAllUserSpecialties_ShouldCallRepository() {
        // Arrange
        doNothing().when(userSpecialtyRepository).deleteAllByUserId(userId);

        // Act
        userSpecialtyService.removeAllUserSpecialties(userId);

        // Assert
        verify(userSpecialtyRepository).deleteAllByUserId(userId);
    }
}
