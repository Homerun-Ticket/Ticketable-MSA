package com.example.modulepoint.domain.payment.service;

import com.example.modulepoint.global.exception.ErrorCode;
import com.example.modulepoint.global.exception.ServerException;
import com.example.modulepoint.domain.payment.entity.PointPayment;
import com.example.modulepoint.domain.payment.enums.PaymentStatus;
import com.example.modulepoint.domain.payment.repository.PointPaymentRepository;
import com.example.modulepoint.domain.point.enums.PointHistoryType;
import com.example.modulepoint.domain.point.service.PointService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointPaymentService {

	private final IamportClient iamportClient;
	private final PointService pointService;
	private final PointPaymentRepository pointPaymentRepository;

	@Transactional
	public IamportResponse<Payment> iamPortPayment(Long memberId, String imp_uid) {
		// 결제 테이블에 해당 imp_uid 가 있는지 확인 (있는 상태일 때 해당 status 가 SUCCESS, FAILED 이면 예외)
		pointPaymentRepository.findByImpUid(imp_uid)
			.ifPresent(exists -> {
				if (exists.getStatus().equals(PaymentStatus.SUCCESS)) {
					throw new ServerException(ErrorCode.ALREADY_PAYMENT_SUCCESS);
				}
				if (exists.getStatus().equals(PaymentStatus.FAILED)) {
					throw new ServerException(ErrorCode.ALREADY_PAYMENT_FAILED);
				}
			});
		
		// 우선 imp_uid 에 unique 제약조건이 걸려있기 때문에 PENDING 상태로 우선 저장과 flush
		PointPayment pointPayment = PointPayment.builder()
			.impUid(imp_uid)
			.memberId(memberId)
			.price(null)
			.status(PaymentStatus.PENDING)
			.build();
		pointPaymentRepository.saveAndFlush(pointPayment);
		
		// 결제 수행
		try {
			IamportResponse<Payment> response = iamportClient.paymentByImpUid(imp_uid);
			Payment payment = response.getResponse();
			
			int price = payment.getAmount().intValue();
			String status = payment.getStatus();
			
			if (!status.equals("paid")) {
				pointPayment.changeStatus(PaymentStatus.FAILED);
				pointPaymentRepository.save(pointPayment);
				throw new ServerException(ErrorCode.PAYMENT_NOT_SUCCESS);
			}
			
			pointService.increasePoint(memberId, price, PointHistoryType.FILL);
			
			pointPayment.changeStatus(PaymentStatus.SUCCESS);
			pointPayment.setPrice(price);
			pointPaymentRepository.save(pointPayment);
			
			return response;
		} catch(Exception e) {
			// 네트워크 or DB 에러 상황 시 FAILED
			pointPayment.changeStatus(PaymentStatus.FAILED);
			pointPaymentRepository.save(pointPayment);
			throw new ServerException(ErrorCode.PAYMENT_VALID_ERROR);
		}
	}
}
