package com.example.moduleauction.domain.auction.repository;

import com.example.moduleauction.domain.auction.dto.request.AuctionSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuctionRepositoryQuery {
	Page<Long> findByConditions(AuctionSearchCondition dto, Pageable pageable);
}
