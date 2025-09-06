package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "UF", length = 2)
    private String uf;
    
    @Column(name = "city", length = 255)
    private String city;
    
    @OneToOne
    @JoinColumn(name = "fk_location_user_id_user", nullable = false)
    private User user;

    public Location(String uf, String city, User user) {
        this.uf = uf;
        this.city = city;
        this.user = user;
    }

    public Location(String uf, String city) {
        this.uf = uf;
        this.city = city;
    }
}
