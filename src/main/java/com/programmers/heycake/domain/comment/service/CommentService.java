package com.programmers.heycake.domain.comment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public List<CommentResponse> getComments(Long offerId) {
		return commentRepository.findByOfferId(offerId)
				.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

}
