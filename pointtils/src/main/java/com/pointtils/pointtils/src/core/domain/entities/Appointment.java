package com.pointtils.pointtils.src.core.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentModality;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "appointment")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {

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

    @Column(name = "street")
    private String street;

    @Column(name = "street_number")
    private Integer streetNumber;

    @Column(name = "address_details")
    private String addressDetails;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "modality", nullable = false)
    private AppointmentModality modality;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @ManyToOne
    @JoinColumn(name = "interpreter_id", nullable = false)
    private Interpreter interpreter;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt; 


    public Appointment(String uf, String city, AppointmentModality modality, LocalDate date, String description,
                       AppointmentStatus status, Interpreter interpreterId, User userId, LocalTime startTime, LocalTime endTime){
        this.uf = uf;
        this.city = city;
        this.modality = modality;
        this.date = date;
        this.description = description;
        this.status = status;
        this.interpreter = interpreterId;
        this.user = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
