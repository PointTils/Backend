package com.pointtils.pointtils.src.application.services;

import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.mapper.PersonResponseMapper;
import com.pointtils.pointtils.src.core.domain.entities.Person;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.infrastructure.repositories.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonCreationRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonPatchRequest;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonResponse;
import static com.pointtils.pointtils.src.util.TestDataUtil.createPersonUpdateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonResponseMapper personResponseMapper;
    @InjectMocks
    private PersonService personService;

    @Test
    void shouldRegisterPerson() {
        final ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        PersonResponseDTO mockResponse = createPersonResponse();

        when(passwordEncoder.encode("senha123")).thenReturn("encodedPassword");
        when(personRepository.save(personArgumentCaptor.capture())).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });
        when(personResponseMapper.toResponseDTO(any())).thenReturn(mockResponse);

        assertEquals(mockResponse, personService.registerPerson(createPersonCreationRequest()));
        assertEquals("João Pessoa", personArgumentCaptor.getValue().getName());
        assertEquals("pessoa@exemplo.com", personArgumentCaptor.getValue().getEmail());
        assertEquals("encodedPassword", personArgumentCaptor.getValue().getPassword());
        assertEquals("MALE", personArgumentCaptor.getValue().getGender().name());
        assertEquals("51999999999", personArgumentCaptor.getValue().getPhone());
        assertEquals("11122233344", personArgumentCaptor.getValue().getCpf());
        assertEquals("picture_url", personArgumentCaptor.getValue().getPicture());
        assertEquals("1990-01-01", personArgumentCaptor.getValue().getBirthday().toString());
        assertEquals("PERSON", personArgumentCaptor.getValue().getType().name());
        assertEquals("ACTIVE", personArgumentCaptor.getValue().getStatus().name());
    }

    @Test
    void shouldFindPersonById() {
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();

        when(personRepository.findById(personId)).thenReturn(Optional.of(new Person()));
        when(personResponseMapper.toResponseDTO(any())).thenReturn(mockResponse);

        assertEquals(mockResponse, personService.findById(personId));
    }

    @Test
    void shouldThrowExceptionIfPersonWasNotFoundById() {
        UUID personId = UUID.randomUUID();
        when(personRepository.findById(personId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> personService.findById(personId));
        verifyNoInteractions(personResponseMapper);
    }

    @Test
    void shouldFindAllPeople() {
        when(personRepository.findAllByType(UserTypeE.PERSON)).thenReturn(List.of(new Person()));
        when(personResponseMapper.toResponseDTO(any())).thenReturn(createPersonResponse());

        assertThat(personService.findAll())
                .hasSize(1)
                .anyMatch(response -> "João Pessoa".equals(response.getName())
                        && "pessoa@exemplo.com".equals(response.getEmail())
                        && "51999999999".equals(response.getPhone())
                        && "MALE".equals(response.getGender().name())
                        && "11122233344".equals(response.getCpf())
                        && "picture_url".equals(response.getPicture())
                        && "1990-01-01".equals(response.getBirthday().toString())
                        && "PERSON".equals(response.getType())
                        && "ACTIVE".equals(response.getStatus())
                );
    }

    @Test
    void shouldDeletePersonById() {
        final ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        UUID personId = UUID.randomUUID();

        when(personRepository.findById(personId)).thenReturn(Optional.of(new Person()));
        when(personRepository.save(personArgumentCaptor.capture())).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        assertDoesNotThrow(() -> personService.delete(personId));
        assertEquals("INACTIVE", personArgumentCaptor.getValue().getStatus().name());
        verifyNoInteractions(personResponseMapper);
    }

    @Test
    void shouldUpdatePersonById() {
        final ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        UUID personId = UUID.randomUUID();
        PersonDTO request = createPersonUpdateRequest();
        PersonResponseDTO mockResponse = createPersonResponse();

        when(personRepository.findById(personId)).thenReturn(Optional.of(new Person()));
        when(personRepository.save(personArgumentCaptor.capture())).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });
        when(personResponseMapper.toResponseDTO(any())).thenReturn(mockResponse);

        assertEquals(mockResponse, personService.updateComplete(personId, request));
        assertEquals("João Gustavo Pessoa", personArgumentCaptor.getValue().getName());
        assertEquals("joao.pessoa@exemplo.com", personArgumentCaptor.getValue().getEmail());
        assertEquals("OTHERS", personArgumentCaptor.getValue().getGender().name());
        assertEquals("51988888888", personArgumentCaptor.getValue().getPhone());
        assertEquals("22233344455", personArgumentCaptor.getValue().getCpf());
        assertEquals("new_picture", personArgumentCaptor.getValue().getPicture());
        assertEquals("2000-02-15", personArgumentCaptor.getValue().getBirthday().toString());
    }

    @Test
    void shouldPartiallyUpdatePersonByIdWithEmptyData() {
        final ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();

        when(personRepository.findById(personId)).thenReturn(Optional.of(new Person()));
        when(personRepository.save(personArgumentCaptor.capture())).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });
        when(personResponseMapper.toResponseDTO(any())).thenReturn(mockResponse);

        assertEquals(mockResponse, personService.updatePartial(personId, new PersonPatchRequestDTO()));
    }

    @Test
    void shouldPartiallyUpdatePersonById() {
        final ArgumentCaptor<Person> personArgumentCaptor = ArgumentCaptor.forClass(Person.class);
        UUID personId = UUID.randomUUID();
        PersonResponseDTO mockResponse = createPersonResponse();

        when(personRepository.findById(personId)).thenReturn(Optional.of(new Person()));
        when(personRepository.save(personArgumentCaptor.capture())).thenAnswer(invocation -> {
            Person p = invocation.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });
        when(personResponseMapper.toResponseDTO(any())).thenReturn(mockResponse);

        assertEquals(mockResponse, personService.updatePartial(personId, createPersonPatchRequest()));
        assertEquals("João Gustavo Pessoa", personArgumentCaptor.getValue().getName());
        assertEquals("joao.pessoa@exemplo.com", personArgumentCaptor.getValue().getEmail());
        assertEquals("OTHERS", personArgumentCaptor.getValue().getGender().name());
        assertEquals("51988888888", personArgumentCaptor.getValue().getPhone());
        assertEquals("new_picture", personArgumentCaptor.getValue().getPicture());
        assertEquals("2000-02-15", personArgumentCaptor.getValue().getBirthday().toString());
    }
}
