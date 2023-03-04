package com.programmers.heycake.domain.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.comment.facade.CommentFacade;
import com.programmers.heycake.domain.comment.model.dto.request.CommentsRequest;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentFacade commentFacade;
	
	@GetMapping
	public ResponseEntity<List<CommentSummaryResponse>> getComments(@RequestBody CommentsRequest commentsRequest) {

		List<CommentSummaryResponse> comments = commentFacade.getComments(commentsRequest.offerId());
		return ResponseEntity.ok(comments);
	}
}
