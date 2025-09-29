package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentResponseDTO {

    private UUID id;
    private String uf;
    private String city;
    private String neighborhood;
    private String street;
    @JsonProperty("street_number")
    private Integer streetNumber;
    @JsonProperty("address_details")
    private String addressDetails;
    @Schema(allowableValues = {"ONLINE", "PERSONALLY"}, example = "ONLINE")
    private String modality;
    @Schema(type = "string", format = "date", example = "2025-09-23")
    private String date;
    private String description;
    @Schema(allowableValues = {"PENDING", "ACCEPTED", "CANCELED", "COMPLETED"}, example = "PENDING")
    private String status;
    @JsonProperty("interpreter_id")
    private UUID interpreterId;
    @JsonProperty("user_id")
    private UUID userId;
    @JsonProperty("start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "09:30:00")
    private LocalTime startTime;
    @JsonProperty("end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "10:30:00")
    private LocalTime endTime;

}
