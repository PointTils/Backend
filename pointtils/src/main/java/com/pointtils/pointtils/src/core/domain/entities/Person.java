package com.pointtils.pointtils.src.core.domain.entities;

import java.time.LocalDate;

import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.persistence.Column;
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

@Table(name = "person")
@Entity
@PrimaryKeyJoinColumn(name = "id")
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
}
