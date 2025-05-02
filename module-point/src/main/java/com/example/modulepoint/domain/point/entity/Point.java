package com.example.modulepoint.domain.point.entity;

import static com.example.modulepoint.global.exception.ErrorCode.NOT_ENOUGH_POINT;

import com.example.modulepoint.global.exception.ServerException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Point {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer point;
	
	@Column(nullable = false)
	private Long memberId;

	@Builder
	public Point(Integer point, Long memberId) {
		this.point = point;
		this.memberId = memberId;
	}

	public void plusPoint(Integer charge) {
		this.point += charge;
	}

	public void minusPoint(Integer charge) {
		if (this.point < charge) {
			throw new ServerException(NOT_ENOUGH_POINT);
		}
		this.point -= charge;
	}
}
