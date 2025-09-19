package com.pointtils.pointtils.src.core.domain.entities;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "location")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Location {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "UF", length = 2)
    private String uf;

    @Column(name = "city", length = 255)
    private String city;

    @Column(name = "neighborhood")
    private String neighborhood;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Location(String uf, String city, String neighborhood, User user) {
        this.uf = uf;
        this.city = city;
        this.user = user;
        this.neighborhood = neighborhood;
    }

    public Location(String uf, String city, String neighborhood) {
        this.uf = uf;
        this.city = city;
        this.neighborhood = neighborhood;
    }
}
