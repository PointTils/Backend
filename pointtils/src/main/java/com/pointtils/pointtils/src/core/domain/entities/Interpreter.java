package com.pointtils.pointtils.src.core.domain.entities;

import java.math.BigDecimal;

import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interpreter")
@PrimaryKeyJoinColumn(name = "id")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Interpreter extends Person {
    
    private String cnpj;
    private Double rating;

    @Column(name = "min_value")
    private BigDecimal minValue;

    @Column(name = "max_value")
    private BigDecimal maxValue;

    @Column(name = "image_rights")
    private Boolean imageRights;

    @Enumerated(EnumType.STRING)
    private InterpreterModality modality;

    @Column(columnDefinition = "TEXT")
    private String description;
}