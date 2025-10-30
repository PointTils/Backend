package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "parameters")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Parameters {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "key", columnDefinition = "TEXT", nullable = false)
    private String key;

    @Column(name = "value", columnDefinition = "TEXT", nullable = false)
    private String value;
}
