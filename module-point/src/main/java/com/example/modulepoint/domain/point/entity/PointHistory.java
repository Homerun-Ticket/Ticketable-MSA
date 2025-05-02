package com.example.modulepoint.domain.point.entity;

import com.example.modulepoint.global.entity.Timestamped;
import com.example.modulepoint.domain.point.enums.PointHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class PointHistory extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer charge;

	@Column(length = 50)
	@Enumerated(EnumType.STRING)
	private PointHistoryType type;

	@Column(nullable = false)
	private Long memberId;

	@Builder
	public PointHistory(Integer charge, PointHistoryType type, Long memberId) {
		this.charge = charge;
		this.type = type;
		this.memberId = memberId;
	}
}
