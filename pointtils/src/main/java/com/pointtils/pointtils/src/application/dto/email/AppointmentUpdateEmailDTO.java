package com.pointtils.pointtils.src.application.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentUpdateEmailDTO {
    private String email;
    private String template;
    private String userName;
    private String appointmentDate;
    private String appointmentDescription;
    private String appointmentModality;
    private String appointmentLocation;
    private String subject;
    private String subjectName;
}
