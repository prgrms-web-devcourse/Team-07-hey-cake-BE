package com.programmers.heycake.domain.comment.controller;

import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.comment.facade.CommentFacade;
import com.programmers.heycake.domain.comment.model.dto.request.CommentSaveRequest;
import com.programmers.heycake.domain.comment.model.dto.response.CommentSummaryResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentFacade commentFacade;

	@PostMapping
	public ResponseEntity<Void> saveComment(@ModelAttribute @Valid CommentSaveRequest commentSaveRequest,
			HttpServletRequest httpServletRequest) {

		Long savedCommentId = commentFacade.saveComment(commentSaveRequest);

		return ResponseEntity.created(URI.create(httpServletRequest.getRequestURI() + savedCommentId)).build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
		commentFacade.deleteComment(commentId);

		return ResponseEntity.noContent()
				.build();
	}

	@GetMapping
	public ResponseEntity<List<CommentSummaryResponse>> getComments(@RequestParam Long offerId) {

		List<CommentSummaryResponse> comments = commentFacade.getComments(offerId);
		return ResponseEntity.ok(comments);
	}
}
