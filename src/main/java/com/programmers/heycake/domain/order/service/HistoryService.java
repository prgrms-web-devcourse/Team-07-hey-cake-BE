package com.programmers.heycake.domain.order.service;

import static com.programmers.heycake.common.mapper.HistoryMapper.*;

import org.springframework.stereotype.Service;

import com.programmers.heycake.domain.order.model.vo.request.HistoryRequest;
import com.programmers.heycake.domain.order.repository.HistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
	private final HistoryRepository historyRepository;

	public Long createHistory(HistoryRequest historyRequest) {
		return historyRepository.save(
				toOrderHistory(historyRequest)
		).getId();
	}
}
