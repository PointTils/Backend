package com.pointtils.pointtils.src.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.pointtils.pointtils.src.core.domain.entities.Enterprise;
import com.pointtils.pointtils.src.core.domain.entities.enums.UserTypeE;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@JsonPropertyOrder({
		"id",
		"corporate_reason",
		"cnpj",
		"email",
		"type",
		"status",
		"phone",
		"picture"
})
public class EnterpriseResponseDTO {

	private UUID id;
	@JsonProperty("corporate_reason")
	private String corporateReason;
	private String cnpj;
	private String email;
	private UserTypeE type;
	private String status;
	private String phone;
	private String picture;

	public EnterpriseResponseDTO(Enterprise enterprise) {
		this.id = enterprise.getId();
		this.corporateReason = enterprise.getCorporateReason();
		this.cnpj = enterprise.getCnpj();
		this.email = enterprise.getEmail();
		this.type = enterprise.getType();
		this.status = enterprise.getStatus().name();
		this.phone = enterprise.getPhone();
		this.picture = enterprise.getPicture();
	}
}
