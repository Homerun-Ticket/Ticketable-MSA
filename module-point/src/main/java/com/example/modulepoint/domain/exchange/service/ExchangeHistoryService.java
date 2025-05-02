package com.example.modulepoint.domain.exchange.service;


import com.example.modulepoint.global.exception.ServerException;
import com.example.modulepoint.domain.exchange.dto.response.ExchangeHistoryResponse;
import com.example.modulepoint.domain.exchange.dto.response.ExchangeResponse;
import com.example.modulepoint.domain.point.entity.Point;
import com.example.modulepoint.domain.exchange.entity.ExchangeHistory;
import com.example.modulepoint.domain.exchange.enums.ExchangeHistoryType;
import com.example.modulepoint.domain.exchange.repository.ExchangeHistoryRepository;
import com.example.modulepoint.domain.point.enums.PointHistoryType;
import com.example.modulepoint.domain.point.repository.PointRepository;
import com.example.modulepoint.domain.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.modulepoint.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ExchangeHistoryService {

	private final ExchangeHistoryRepository exchangeHistoryRepository;
	private final PointRepository pointRepository;
	private final PointHistoryService pointHistoryService;
	
	@Transactional
	public void createPointExchangeHistory(Long memberId, ExchangeHistoryType type, Integer charge) {
		ExchangeHistory exchangeHistory = ExchangeHistory.builder()
			.memberId(memberId)
			.type(type)
			.charge(charge)
			.build();
		
		exchangeHistoryRepository.save(exchangeHistory);
	}

	@Transactional
	public ExchangeResponse exchangePoint(Long pointExchangeId) {
		ExchangeHistory exchangeHistory = exchangeHistoryRepository.findById(pointExchangeId)
			.orElseThrow(() -> new ServerException(POINT_EXCHANGE_NOT_FOUND));

		if (!exchangeHistory.getType().equals(ExchangeHistoryType.EXCHANGE_REQUEST)) {
			throw new ServerException(EXCHANGE_REQUEST_NOT_STATE);
		}
		
		Long memberId = exchangeHistory.getMemberId();
		Point point = pointRepository.findByMemberId(memberId)
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));

		exchangeHistory.exchange();
		pointHistoryService.createPointHistory(exchangeHistory.getCharge(), PointHistoryType.EXCHANGE, memberId);

		return new ExchangeResponse(
			memberId, exchangeHistory.getCharge(), point.getPoint(), exchangeHistory.getType().toString()
		);
	}

	@Transactional(readOnly = true)
	public ExchangeHistoryResponse getExchangeHistory(Long exchangeHistoryId) {
		ExchangeHistory exchangeHistory = findExchangeHistory(exchangeHistoryId);
		return ExchangeHistoryResponse.of(exchangeHistory);
	}

	@Transactional(readOnly = true)
	public PagedModel<ExchangeHistoryResponse> getExchangeHistories(int page) {
		Pageable pageable = PageRequest.of(page - 1, 10,
			Sort.by(Sort.Direction.ASC, "createdAt"));

		Page<ExchangeHistory> points = exchangeHistoryRepository
			.findAllByType(ExchangeHistoryType.EXCHANGE_REQUEST, pageable);

		return new PagedModel<>(points.map(ExchangeHistoryResponse::of));
	}

	private ExchangeHistory findExchangeHistory(Long exchangeHistoryId) {
		return exchangeHistoryRepository.findById(exchangeHistoryId)
			.orElseThrow(() -> new ServerException(POINT_EXCHANGE_NOT_FOUND));
	}
}
