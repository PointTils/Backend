package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "enterprise")
@PrimaryKeyJoinColumn(name = "id")
public class Enterprise extends User {
    @Column(name = "corporate_reason", nullable = false)
    private String corporateReason;

    @Column(unique = true, length = 14)
    private String cnpj;
}
