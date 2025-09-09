package com.pointtils.pointtils.src.core.domain.entities;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Table(name = "person")
@Entity
@PrimaryKeyJoinColumn(name = "id")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Person extends User {
    
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Column(unique = true, length = 11)
    private String cpf;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getType() {
        return "person";
    }
}
