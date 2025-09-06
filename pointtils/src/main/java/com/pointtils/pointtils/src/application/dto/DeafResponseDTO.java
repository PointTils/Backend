package com.pointtils.pointtils.src.application.dto;

import java.time.LocalDate;

import com.pointtils.pointtils.src.application.mapper.AccessibilityMapper;
import com.pointtils.pointtils.src.application.mapper.LocationMapper;
import com.pointtils.pointtils.src.core.domain.entities.Person;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeafResponseDTO {

    private Long id;
    private String email;
    private String phone;
    private String picture;
    private String status;
    private String type;
    private String name;
    private String gender;
    private String birthday;
    private String cpf;
    private LocationDTO location;
    private AccessibilityPreferencesDTO accessibility_preferences;
    private LocalDate created_at = LocalDate.now();
    
        public DeafResponseDTO(Person person) {
        this.id = person.getId();
        this.email = person.getEmail();
        this.phone = person.getPhone();
        this.picture = person.getPicture();
        this.status = person.getStatus().name();
        this.type = person.getType().name();
        this.name = person.getName();
        this.gender = person.getGender() != null ? person.getGender().name() : null;
        this.birthday = person.getBirthday() != null ? person.getBirthday().toString() : null;
        this.cpf = person.getCpf();
        this.location = LocationMapper.toDto(person.getLocation());
        this.accessibility_preferences = AccessibilityMapper.toDto(person.getAp());
    }

    
}
