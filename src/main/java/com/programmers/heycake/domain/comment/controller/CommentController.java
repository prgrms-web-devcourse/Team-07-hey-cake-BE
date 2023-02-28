package com.programmers.heycake.domain.comment.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programmers.heycake.domain.comment.facade.CommentFacade;
import com.programmers.heycake.domain.comment.model.dto.request.CommentSaveRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentFacade commentFacade;

	@PostMapping
	public ResponseEntity<Void> saveComment(@ModelAttribute @Valid CommentSaveRequest commentSaveRequest) {

		Long savedCommentId = commentFacade.saveComment(commentSaveRequest);

		return ResponseEntity.created(URI.create("/api/v1" + savedCommentId)).build();
	}
}
