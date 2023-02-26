package com.programmers.heycake.domain.order.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.HistoryFacade;
import com.programmers.heycake.domain.order.model.vo.request.HistoryControllerRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryFacade historyFacade;

	/**
	 * 해야할일
	 * 멤버에서 가져온 memberID와 orderId 작성자가 같은지 확인
	 * 스레드 id가 orderId를 보고있는지 -> 페치조인으로 오더가져오기
	 * 스레드 id의 업주id가져오기
	 * 모두맞으면 엔티티 생성
	 */
	@PostMapping
	public ResponseEntity<Void> createHistory(HistoryControllerRequest historyRequest) {
		Long historyId = historyFacade.createHistory(historyRequest);

		URI location = URI.create("/api/v1/histories" + historyId);
		return ResponseEntity.created(location).build();
	}

}
