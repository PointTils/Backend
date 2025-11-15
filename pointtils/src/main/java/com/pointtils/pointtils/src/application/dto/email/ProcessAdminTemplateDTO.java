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
public class ProcessAdminTemplateDTO {
    private String template;
    private String interpreterName;
    private String cpf;
    private String cnpj;
    private String email;
    private String phone;
    private String videoUrl;
    private String acceptLink;
    private String rejectLink;
}
