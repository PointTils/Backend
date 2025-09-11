package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enterprise")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Enterprise extends User {

    @Column(name = "corporate_reason", nullable = false)
    private String corporateReason;

    @Column(unique = true, length = 14)
    private String cnpj;

    @Override
    public String getDisplayName() {
        return corporateReason;
    }
}
