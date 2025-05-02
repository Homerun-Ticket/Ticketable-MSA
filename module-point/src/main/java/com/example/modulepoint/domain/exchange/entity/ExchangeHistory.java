package com.example.modulepoint.domain.exchange.entity;

import com.example.modulepoint.global.entity.Timestamped;
import com.example.modulepoint.global.exception.ErrorCode;
import com.example.modulepoint.global.exception.ServerException;
import com.example.modulepoint.domain.exchange.enums.ExchangeHistoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeHistory extends Timestamped {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long memberId;
	
	@Enumerated(EnumType.STRING)
	private ExchangeHistoryType type;
	
	private Integer charge;
	
	@Builder
	public ExchangeHistory(Long memberId, ExchangeHistoryType type, Integer charge) {
		this.memberId = memberId;
		this.type = type;
		this.charge = charge;
	}
	
	public void exchange() {
		if (this.type.equals(ExchangeHistoryType.EXCHANGE)) {
			throw new ServerException(ErrorCode.ALREADY_EXCHANGE_STATE);
		}
		this.type = ExchangeHistoryType.EXCHANGE;
	}
}
