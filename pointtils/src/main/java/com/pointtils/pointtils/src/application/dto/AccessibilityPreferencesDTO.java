package com.pointtils.pointtils.src.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessibilityPreferencesDTO {

    @JsonProperty("communication_method")
    private String communication;
    
    @JsonProperty("preferred_modality")
    private String modality;
    
    @JsonProperty("interpreter_gender_preference")
    private String accessGender;
    
    @JsonProperty("emergency_contact")
    private EmergencyContactDTO emergency;
    

}
