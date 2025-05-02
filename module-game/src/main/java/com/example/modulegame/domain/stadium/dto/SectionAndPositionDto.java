package com.example.modulegame.domain.stadium.dto;

import java.util.List;

import com.example.modulegame.domain.stadium.entity.Seat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionAndPositionDto {
	private String type;
	private String code;
	private List<String> positions;

	public static SectionAndPositionDto from(List<Seat> seats) {
		return new SectionAndPositionDto(
			seats.get(0).getSection().getType(),
			seats.get(0).getSection().getCode(),
			seats.stream().map(Seat::getPosition).toList()
		);
	}
}
