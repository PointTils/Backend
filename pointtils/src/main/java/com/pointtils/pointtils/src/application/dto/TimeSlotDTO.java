package com.pointtils.pointtils.src.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {

    private Date date;
    private UUID interpreterId;
    private LocalTime startTime;
    private LocalTime endTime;
}
