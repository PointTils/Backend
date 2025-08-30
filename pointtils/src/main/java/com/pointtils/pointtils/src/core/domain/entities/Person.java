package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "person")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Person extends User {
    private String name;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getType() {
        return "person";
    }
}
