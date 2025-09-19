package com.pointtils.pointtils.src.core.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "schedule")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "interpreter_id", nullable = false)
    private UUID interpreterId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private DayOfWeek day;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

}
