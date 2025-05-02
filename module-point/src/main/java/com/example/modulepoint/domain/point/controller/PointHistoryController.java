package com.example.modulepoint.domain.point.controller;

import com.example.modulepoint.global.annotation.LoginUser;
import com.example.modulepoint.global.entity.AuthUser;
import com.example.modulepoint.domain.point.dto.response.PointHistoryResponse;
import com.example.modulepoint.domain.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PointHistoryController {

	private final PointHistoryService pointHistoryService;

	@GetMapping("/v1/pointHistories")
	public ResponseEntity<PagedModel<PointHistoryResponse>> getPointHistories(
		@LoginUser AuthUser authUser,
		@RequestParam int page
	) {
		return ResponseEntity.ok(pointHistoryService.getPointHistories(authUser.getMemberId(), page));
	}
}
