package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseRequestDTO {
	@NotBlank(message = "Razão social deve ser preenchida")
	@JsonProperty("corporate_reason")
	private String corporateReason;
	
	@NotBlank(message = "CNPJ deve ser preenchido")
	@Pattern(regexp = "^\\d{14}$", message = "CNPJ inválido")
	private String cnpj;
	
	@NotBlank(message = "Email deve ser preenchido")
	@Email(message = "Email inválido")
	private String email;
	
	@NotBlank(message = "Senha deve ser preenchida")
	private String password;
	
	@NotBlank(message = "Número de telefone deve ser preenchido")
	@Pattern(regexp = "^\\d+$", message = "Número de telefone inválido")
	private String phone;
	
	private String picture;
}
