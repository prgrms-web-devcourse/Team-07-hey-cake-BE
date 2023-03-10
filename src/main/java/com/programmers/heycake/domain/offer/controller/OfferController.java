package com.programmers.heycake.domain.offer.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.offer.facade.OfferFacade;
import com.programmers.heycake.domain.offer.model.dto.request.OfferSaveRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferSummaryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OfferController {

	private final OfferFacade offerFacade;

	@PostMapping("/api/v1/offers")
	public ResponseEntity<Void> saveOffer(@Valid @ModelAttribute OfferSaveRequest offerSaveRequest) {

		Long savedOfferId = offerFacade.saveOffer(offerSaveRequest);
		return ResponseEntity.created(URI.create("/api/v1/offers/" + savedOfferId)).build();
	}

	@DeleteMapping("/api/v1/offers/{offerId}")
	public ResponseEntity<Void> deleteOffer(@PathVariable Long offerId) {
		offerFacade.deleteOffer(offerId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/v1/orders/{orderId}/offers")
	public ResponseEntity<List<OfferSummaryResponse>> getOffers(@PathVariable Long orderId) {

		return ResponseEntity.ok(offerFacade.getOffers(orderId));
	}
}
