package com.example.modulepoint.domain.payment.entity;

import com.example.modulepoint.global.entity.Timestamped;
import com.example.modulepoint.domain.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointPayment extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String impUid;

	@Setter
	private Integer price;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	@Column(nullable = false)
	private Long memberId;

	@Builder
	public PointPayment(String impUid, Integer price, PaymentStatus status, Long memberId) {
		this.impUid = impUid;
		this.price = price;
		this.status = status;
		this.memberId = memberId;
	}
	
	public void changeStatus(PaymentStatus status) {
		this.status = status;
	}
}
