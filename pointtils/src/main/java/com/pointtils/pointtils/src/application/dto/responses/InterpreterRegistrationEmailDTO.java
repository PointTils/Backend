package com.pointtils.pointtils.src.application.dto.responses;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InterpreterRegistrationEmailDTO {
    private String adminEmail;
    private String interpreterName;
    private String cpf;
    private String cnpj;
    private String email;
    private String phone;
    private String acceptLink;
    private String rejectLink;
    private List<MultipartFile> files;
}
