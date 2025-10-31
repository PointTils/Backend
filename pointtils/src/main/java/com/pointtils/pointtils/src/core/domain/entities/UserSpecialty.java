package com.pointtils.pointtils.src.core.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "user_specialties")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSpecialty {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "specialtie_id", nullable = false)
    private Specialty specialty;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

     @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; 

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt; 
    
    public UserSpecialty(Specialty specialty, User user) {
        this.specialty = specialty;
        this.user = user;
    }
}
