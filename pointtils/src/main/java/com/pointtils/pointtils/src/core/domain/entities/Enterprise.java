package com.pointtils.pointtils.src.core.domain.entities;

import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "enterprise")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Enterprise extends User {

    @Column(name = "corporate_reason", nullable = false)
    private String corporateReason;

    @Column(unique = true, length = 14)
    private String cnpj;

    @Override
    public String getDisplayName() {
        return corporateReason;
    }

    @Override
    public UserTypeE getType() {

        return UserTypeE.ENTERPRISE;
    }
}
