package com.example.modulegame.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatDto {
	private Long seatId;
	private String position;
	private int SectionExtraCharge;
	private int price;
}
