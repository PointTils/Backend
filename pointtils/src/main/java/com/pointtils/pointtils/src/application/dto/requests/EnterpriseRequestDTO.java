package com.pointtils.pointtils.src.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pointtils.pointtils.src.application.dto.LocationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	@NotBlank(message = "Corporate reason is required")
	@JsonProperty("corporate_reason")
	private String corporateReason;
	
	@NotBlank(message = "CNPJ is required")
	@Pattern(regexp = "^\\d{14}$", message = "Invalid CNPJ")
	private String cnpj;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Invalid Email")
	private String email;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	@NotBlank(message = "Phone is required")
	@Pattern(regexp = "^\\d+$", message = "Invalid Phone")
	private String phone;
	
	private String picture;
	
	@NotNull(message = "Location is required")
	@Valid
	private LocationDTO location;
}
