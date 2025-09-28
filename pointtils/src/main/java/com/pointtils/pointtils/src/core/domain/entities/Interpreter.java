package com.pointtils.pointtils.src.core.domain.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "interpreter")
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Interpreter extends Person {

    private String cnpj;

    private BigDecimal rating;

    @Column(name = "min_value")
    private BigDecimal minValue;

    @Column(name = "max_value")
    private BigDecimal maxValue;

    @Column(name = "image_rights")
    private Boolean imageRights;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private InterpreterModality modality;

    @OneToMany(mappedBy = "interpreter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locations;

    @Column(columnDefinition = "TEXT")
    private String description;

    @lombok.Builder.Default
    @OneToMany(mappedBy = "interpreter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Schedule> schedules = new HashSet<>();
}