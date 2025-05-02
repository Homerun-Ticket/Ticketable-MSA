package com.example.modulepoint.domain.point.enums;

public enum PointHistoryType {
	/**
	 * 예매,
	 * 입찰,
	 * 입찰 실패(입찰 환불),
	 * 판매,
	 * 환불,
	 * 충전,
	 * 환전
	 */
	RESERVATION,
	BID,
	BID_REFUND,
	SELL,
	RECOVERY,
	REFUND,
	FILL,
	EXCHANGE_REQUEST,
	EXCHANGE
}
