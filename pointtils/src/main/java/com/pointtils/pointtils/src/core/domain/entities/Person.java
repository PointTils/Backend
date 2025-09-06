package com.pointtils.pointtils.src.core.domain.entities;

import java.time.LocalDate;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Table(name = "person")
@Entity
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Person extends User{
    
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Column(unique = true, length = 11)
    private String cpf;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "communication", column = @Column(name = "accessibility_communication")),
        @AttributeOverride(name = "modality", column = @Column(name = "accessibility_modality")),
        @AttributeOverride(name = "gender", column = @Column(name = "accessibility_gender")),
        @AttributeOverride(name = "emergency.name", column = @Column(name = "emergency_contact_name")),
        @AttributeOverride(name = "emergency.phone", column = @Column(name = "emergency_contact_phone")),
        @AttributeOverride(name = "emergency.relationship", column = @Column(name = "emergency_contact_relationship"))
    })
    private AccessibilityPreferences ap;

}
