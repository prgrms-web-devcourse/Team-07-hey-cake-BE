package com.programmers.heycake.domain.market.controller;

import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.market.model.dto.MarketControllerResponse;
import com.programmers.heycake.domain.market.service.MarketFacade;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/markets")
@RequiredArgsConstructor
public class MarketController {

	private final MarketFacade marketFacade;

	@GetMapping("/{marketId}")
	public ResponseEntity<MarketControllerResponse> getMarket(@PathVariable @Positive Long marketId) {
		MarketControllerResponse market = marketFacade.getMarket(marketId);
		return ResponseEntity.ok(market);
	}
}
