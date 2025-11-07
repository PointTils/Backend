package com.pointtils.pointtils.src.util;

import com.pointtils.pointtils.src.application.dto.LocationDTO;
import com.pointtils.pointtils.src.application.dto.PersonDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.InterpreterPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.LocationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonCreationRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.PersonPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataBasicRequestDTO;
import com.pointtils.pointtils.src.application.dto.requests.ProfessionalDataPatchRequestDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.PersonResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataListResponseDTO;
import com.pointtils.pointtils.src.application.dto.responses.ProfessionalDataResponseDTO;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestDataUtil {

    public static PersonCreationRequestDTO createPersonCreationRequest() {
        PersonCreationRequestDTO request = new PersonCreationRequestDTO();
        request.setName("João Pessoa");
        request.setEmail("pessoa@exemplo.com");
        request.setPassword("senha123");
        request.setPhone("51999999999");
        request.setGender(Gender.MALE);
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCpf("11122233344");
        request.setPicture("picture_url");
        return request;
    }

    public static PersonDTO createPersonUpdateRequest() {
        PersonDTO request = new PersonDTO();
        request.setName("João Gustavo Pessoa");
        request.setEmail("joao.pessoa@exemplo.com");
        request.setPhone("51988888888");
        request.setGender(Gender.OTHERS);
        request.setBirthday(LocalDate.of(2000, 2, 15));
        request.setCpf("22233344455");
        request.setPicture("new_picture");
        request.setStatus(UserStatus.ACTIVE);
        request.setType(UserTypeE.PERSON);
        return request;
    }

    public static PersonPatchRequestDTO createPersonPatchRequest() {
        PersonPatchRequestDTO request = new PersonPatchRequestDTO();
        request.setName("João Gustavo Pessoa");
        request.setEmail("joao.pessoa@exemplo.com");
        request.setPhone("51988888888");
        request.setGender(Gender.OTHERS);
        request.setBirthday(LocalDate.of(2000, 2, 15));
        request.setCpf("22233344455");
        request.setPicture("new_picture");
        return request;
    }

    public static PersonResponseDTO createPersonResponse() {
        PersonResponseDTO response = new PersonResponseDTO();
        response.setId(UUID.randomUUID());
        response.setEmail("pessoa@exemplo.com");
        response.setName("João Pessoa");
        response.setType(UserTypeE.PERSON.name());
        response.setStatus(UserStatus.ACTIVE.name());
        response.setPhone("51999999999");
        response.setPicture("picture_url");
        response.setGender(Gender.MALE);
        response.setBirthday(LocalDate.of(1990, 1, 1));
        response.setCpf("11122233344");
        return response;
    }

    public static InterpreterBasicRequestDTO createInterpreterCreationRequest() {
        InterpreterBasicRequestDTO request = new InterpreterBasicRequestDTO();
        request.setName("João Intérprete");
        request.setEmail("interpreter@exemplo.com");
        request.setPassword("senha123");
        request.setPhone("51999999999");
        request.setGender(Gender.MALE);
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCpf("12345678901");
        request.setPicture("picture_url");
        request.setProfessionalData(new ProfessionalDataBasicRequestDTO("12345678000195",
                true,
                InterpreterModality.PERSONALLY,
                "Intérprete experiente em LIBRAS",
		        "https://www.youtube.com/watch?v=tmIBzgKEz3o"));
        return request;
    }

    public static InterpreterResponseDTO createInterpreterResponse() {
        ProfessionalDataResponseDTO professionalInfo = ProfessionalDataResponseDTO.builder()
                .cnpj(null)
                .rating(new BigDecimal("0.0"))
                .imageRights(false)
                .modality(null)
                .description(null)
                .videoUrl(null)
                .build();

        return InterpreterResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type(UserTypeE.INTERPRETER.name())
                .status(UserStatus.PENDING.name())
                .phone("51999999999")
                .picture("picture_url")
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .locations(List.of(
                        new LocationDTO(UUID.randomUUID(), "RS", "Porto Alegre", "São João")))
                .specialties(Collections.emptyList())
                .professionalData(professionalInfo)
                .build();
    }

    public static InterpreterListResponseDTO createInterpreterListResponse() {
        ProfessionalDataListResponseDTO professionalInfo = ProfessionalDataListResponseDTO.builder()
                .rating(new BigDecimal("0.0"))
                .modality(InterpreterModality.PERSONALLY)
                .build();

        return InterpreterListResponseDTO.builder()
                .id(UUID.randomUUID())
                .name("João Intérprete")
                .locations(List.of(new LocationDTO(UUID.randomUUID(), "RS", "Porto Alegre", "São João")))
                .picture("picture_url")
                .professionalData(professionalInfo)
                .build();
    }

    public static InterpreterResponseDTO createInterpreterResponseWithProfessionalData() {
        ProfessionalDataResponseDTO professionalInfo = ProfessionalDataResponseDTO.builder()
                .cnpj("12345678000195")
                .rating(new BigDecimal("0.0"))
                .imageRights(true)
                .modality(InterpreterModality.PERSONALLY)
                .description("Intérprete experiente em LIBRAS")
                .videoUrl("https://www.youtube.com/watch?v=tmIBzgKEz3o")
                .build();

        return InterpreterResponseDTO.builder()
                .id(UUID.randomUUID())
                .email("interpreter@exemplo.com")
                .type(UserTypeE.INTERPRETER.name())
                .status(UserStatus.PENDING.name())
                .phone("51999999999")
                .picture("picture_url")
                .name("João Intérprete")
                .gender(Gender.MALE)
                .birthday(LocalDate.of(1990, 1, 1))
                .cpf("12345678901")
                .locations(List.of(
                        new LocationDTO(UUID.randomUUID(), "RS", "Porto Alegre", "São João")))
                .specialties(Collections.emptyList())
                .professionalData(professionalInfo)
                .build();
    }

    public static InterpreterPatchRequestDTO createInterpreterPatchRequest() {
        InterpreterPatchRequestDTO requestDTO = createLocationPatchRequest();
        requestDTO.setName("Novo Nome");
        requestDTO.setEmail("novo.nome@email.com");
        requestDTO.setGender(Gender.FEMALE);
        requestDTO.setPicture("nova foto");
        requestDTO.setPhone("51988888888");
        requestDTO.setBirthday(LocalDate.of(2000, 5, 23));
        requestDTO.setProfessionalData(createInterpreterProfessionalDataPatchRequest());
        return requestDTO;
    }

    public static InterpreterPatchRequestDTO createLocationPatchRequest() {
        InterpreterPatchRequestDTO requestDTO = new InterpreterPatchRequestDTO();
        requestDTO.setLocations(List.of(new LocationRequestDTO("SP", "São Paulo", "Higienópolis")));
        return requestDTO;
    }

    private static ProfessionalDataPatchRequestDTO createInterpreterProfessionalDataPatchRequest() {
        ProfessionalDataPatchRequestDTO professionalData = new ProfessionalDataPatchRequestDTO();
        professionalData.setCnpj("98765432000196");
        professionalData.setDescription("Teste");
        professionalData.setImageRights(Boolean.FALSE);
        professionalData.setModality(InterpreterModality.ONLINE);
        professionalData.setVideoUrl("https://www.youtube.com/watch?v=tmIBzgKEz3o");
        return professionalData;
    }
}
