package com.pointtils.pointtils.src.application.services;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.application.dto.InterpreterRequestDTO;
import com.pointtils.pointtils.src.application.dto.InterpreterResponseDTO;
import com.pointtils.pointtils.src.application.mapper.ProfessionalDataMapper;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Location;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import com.pointtils.pointtils.src.core.domain.entities.enums.WeekDay;
import com.pointtils.pointtils.src.infrastructure.repositories.InterpreterRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class InterpreterRegisterService {

    private final InterpreterRepository repository;
    private final PasswordEncoder passwordEncoder;
    
    public InterpreterResponseDTO register(InterpreterRequestDTO request){
        Interpreter interpreter = Interpreter.builder()
            .email(request.getPersonalData().getEmail())
            .password(passwordEncoder.encode(request.getPersonalData().getPassword()))
            .phone(request.getPersonalData().getPhone())
            .picture(request.getPersonalData().getPicture())
            .status(UserStatus.ACTIVE)
            .type(UserTypeE.INTERPRETER)
            .name(request.getPersonalData().getName())
            .gender(Gender.fromString(request.getPersonalData().getGender()))
            .birthday(request.getPersonalData().getBirthday())
            .cpf(request.getPersonalData().getCpf())
            .cnpj(request.getProfessionalData().getCnpj())
            .rating(request.getProfessionalData().getRating())
            .minValue(request.getProfessionalData().getMinValue())
            .maxValue(request.getProfessionalData().getMaxValue())
            .imageRights(request.getProfessionalData().getImageRights())
            .modality(ProfessionalDataMapper.toInterpreterModality(request.getProfessionalData().getModality()))
            .description(request.getProfessionalData().getDescription())
            .build();
        
        if (request.getLocation() != null) {
            Location location = Location.builder()
            .uf(request.getLocation().getUf())
            .city(request.getLocation().getCity())
            .user(interpreter)
            .build();
        
            interpreter.setLocation(location);
        }

        if (request.getInitialSchedule() != null && !request.getInitialSchedule().isEmpty()) {
            List<Schedule> schedules = request.getInitialSchedule().stream()
                .map(dto -> Schedule.builder()
                    .day(WeekDay.fromString(dto.getDay()))
                    .startTime(LocalTime.parse(dto.getStart()))
                    .endTime(LocalTime.parse(dto.getEnd()))
                    .build()
                )
                .collect(Collectors.toList());
        }

        Interpreter savedInterpreter = repository.save(interpreter);

        return new InterpreterResponseDTO(savedInterpreter);
    }
}
