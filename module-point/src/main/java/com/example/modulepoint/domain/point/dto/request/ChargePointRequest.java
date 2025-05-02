package com.example.modulepoint.domain.point.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChargePointRequest {
	
	@NotNull(message = "포인트를 입력해주세요.")
	private Integer point;
}
