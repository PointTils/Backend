package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.core.domain.entities.User;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void shouldFindUserByEmail() {
        User mockUser = createMockUser();
        when(userRepository.findByEmail("mock@email.com")).thenReturn(mockUser);

        assertEquals(mockUser, userService.findByEmail("mock@email.com"));
    }

    @Test
    void shouldFindUserById() {
        User mockUser = createMockUser();
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        assertEquals(mockUser, userService.findById(userId));
    }

    @Test
    void shouldThrowExceptionIfUserIsNotFoundById() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void shouldUpdateUser() {
        User mockUser = createMockUser();
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        assertEquals(mockUser, userService.updateUser(mockUser));
        verify(userRepository).save(mockUser);
    }

    private User createMockUser() {
        return new User() {
            @Override
            public String getDisplayName() {
                return "Nome Mock";
            }

            @Override
            public String getDocument() {
                return "11122233344";
            }
        };
    }
}
