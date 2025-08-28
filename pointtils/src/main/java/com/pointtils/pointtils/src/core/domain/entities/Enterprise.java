package com.pointtils.pointtils.src.core.domain.entities;

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
    private String corporateReason;

    @Override
    public String getDisplayName() {
        return corporateReason;
    }

    @Override
    public String getType() {
        return "enterprise";
    }
}
