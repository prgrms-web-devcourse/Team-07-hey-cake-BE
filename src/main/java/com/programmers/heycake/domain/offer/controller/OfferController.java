package com.programmers.heycake.domain.offer.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.offer.facade.OfferFacade;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSummaryRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

	private final OfferFacade offerFacade;

	@PostMapping
	public ResponseEntity<Void> saveOffer(@ModelAttribute OfferSaveRequest offerSaveRequest,
			@RequestParam Long memberId) {

		Long savedOfferId = offerFacade.saveOffer(offerSaveRequest, memberId);
		return ResponseEntity.created(URI.create("/api/v1/offers/" + savedOfferId)).build();
	}

	@GetMapping
	public ResponseEntity<List<OfferSummaryResponse>> getOffers(@RequestBody OfferSummaryRequest offerSummaryRequest) {

		return ResponseEntity.ok(offerFacade.getOffers(offerSummaryRequest));
	}
}