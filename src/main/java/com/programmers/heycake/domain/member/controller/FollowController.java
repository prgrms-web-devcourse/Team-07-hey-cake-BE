package com.programmers.heycake.domain.member.controller;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.facade.FollowFacade;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/v1/follows")
@RequiredArgsConstructor
public class FollowController {
	private final FollowFacade followFacade;

	@PostMapping("/{marketId}")
	public ResponseEntity<Void> createFollow(@PathVariable @Positive Long marketId, HttpServletRequest request) {
		Long followId = followFacade.createFollow(marketId);

		URI location = URI.create(request.getRequestURI() + "/" + followId);
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/{marketId}")
	public ResponseEntity<Void> deleteFollow(@PathVariable @Positive Long marketId) {
		followFacade.deleteFollow(marketId);

		return ResponseEntity.noContent().build();
	}

}
