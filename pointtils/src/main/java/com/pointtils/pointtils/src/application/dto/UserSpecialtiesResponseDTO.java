package com.pointtils.pointtils.src.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSpecialtiesResponseDTO {
    private boolean success;
    private String message;
    private Data data;
    
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private List<UserSpecialtyResponseDTO> userSpecialties;
        private Summary summary;
    }
    
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private int totalAdded;
        private int totalUserSpecialties;
        private int duplicatesIgnored;
    }
}
