package com.pointtils.pointtils.src.core.domain.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Or Date

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt; // Or Date

    @Override
    public String getDisplayName() {
        return corporateReason;
    }

    @Override
    public String getDocument() {
        return cnpj;
    }

    @Override
    public UserTypeE getType() {
        return UserTypeE.ENTERPRISE;
    }
}
