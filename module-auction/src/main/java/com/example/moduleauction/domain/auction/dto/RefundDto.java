package com.example.moduleauction.domain.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefundDto {
	private Long memberId;
	private Long price;
}
