package com.pointtils.pointtils.src.application.dto.responses;

import java.util.List;

import com.pointtils.pointtils.src.application.dto.UserSpecialtyDTO;
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
        private List<UserSpecialtyDTO> userSpecialties;
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
