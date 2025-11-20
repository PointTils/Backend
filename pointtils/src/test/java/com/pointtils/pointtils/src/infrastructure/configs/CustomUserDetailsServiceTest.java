package com.pointtils.pointtils.src.infrastructure.configs;

import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o usuário não for encontrado pelo email")
    void shouldThrowExceptionIfUserNotFoundByEmail() {
        when(userRepository.findByEmail("user@email.com")).thenReturn(null);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("user@email.com"));
        assertEquals("Usuário não encontrado com email: user@email.com", exception.getMessage());
    }

    @Test
    @DisplayName("Deve obter detalhes do usuário quando o usuário for encontrado pelo email")
    void shouldGetUserDetails() {
        String email = "user@email.com";
        Person mockUser = Person.builder()
                .email(email)
                .password("password")
                .type(UserTypeE.PERSON)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(mockUser);

        var foundUserDetails = customUserDetailsService.loadUserByUsername(email);
        assertEquals(email, foundUserDetails.getUsername());
        assertEquals("password", foundUserDetails.getPassword());
        assertThat(foundUserDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PERSON");
    }
}
