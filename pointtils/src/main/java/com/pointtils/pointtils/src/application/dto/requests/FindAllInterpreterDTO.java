package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class FindAllInterpreterDTO {
    private String modality;
    private String gender;
    private String city;
    private String uf;
    private String neighborhood;
    private String specialty;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonProperty("available_date")
    private String availableDate;
}
