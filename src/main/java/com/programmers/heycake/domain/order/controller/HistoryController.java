package com.programmers.heycake.domain.order.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.order.facade.HistoryFacade;
import com.programmers.heycake.domain.order.model.dto.request.HistoryControllerRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/histories")
@RequiredArgsConstructor
public class HistoryController {
	private final HistoryFacade historyFacade;

	@PostMapping
	public ResponseEntity<Void> createHistory(HistoryControllerRequest historyRequest, HttpServletRequest request) {
		Long historyId = historyFacade.createHistory(historyRequest);

		URI location = URI.create(request.getRequestURI() + "/" + historyId);
		return ResponseEntity.created(location).build();
	}

}
