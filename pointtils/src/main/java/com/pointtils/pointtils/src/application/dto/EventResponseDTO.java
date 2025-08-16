package com.pointtils.pointtils.src.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponseDTO {
    private String id;
    private String summary;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String htmlLink;
    private String status;
}