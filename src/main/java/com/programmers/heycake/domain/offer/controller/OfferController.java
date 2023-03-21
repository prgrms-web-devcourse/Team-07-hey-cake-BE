package com.programmers.heycake.domain.offer.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.facade.OfferFacade;
import com.programmers.heycake.domain.offer.model.dto.request.OfferCreateRequest;
import com.programmers.heycake.domain.offer.model.dto.response.OfferListResponse;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class OfferController {

	private final OfferFacade offerFacade;

	@PostMapping("/api/v1/offers")
	public ResponseEntity<Void> createOffer(
			@Valid @ModelAttribute OfferCreateRequest offerCreateRequest,
			HttpServletRequest request
	) {
		Long savedOfferId = offerFacade.createOffer(offerCreateRequest);
		return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + savedOfferId)).build();
	}

	@DeleteMapping("/api/v1/offers/{offerId}")
	public ResponseEntity<Void> deleteOffer(@PathVariable @Positive Long offerId) {
		offerFacade.deleteOffer(offerId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/api/v1/orders/{orderId}/offers")
	public ResponseEntity<List<OfferListResponse>> getOffers(@PathVariable Long orderId) {

		return ResponseEntity.ok(offerFacade.getOffers(orderId));
	}
}
