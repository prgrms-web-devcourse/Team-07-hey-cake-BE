package com.programmers.heycake.domain.comment.service;

import static com.programmers.heycake.common.mapper.CommentMapper.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.domain.comment.model.dto.response.CommentResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.repository.CommentRepository;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public Long createComment(String content, Offer offer, Market market, Member member) {
		Order order = offer.getOrder();

		verifyOrderExpired(order);
		verifyCommentWriteAuthority(order, market, member);

		Comment comment = toEntity(member.getId(), content);
		comment.setOffer(offer);

		commentRepository.save(comment);

		return comment.getId();
	}

	private void verifyCommentWriteAuthority(Order order, Market market, Member member) {
		if ((order.isNotWrittenBy(member.getId())) && member.isDifferentMember(market.getMember())) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private void verifyOrderExpired(Order order) {
		if (order.isExpired()) {
			throw new BusinessException(ErrorCode.ORDER_EXPIRED);
		}
	}

	public void deleteComment(Long commentId) {
		Long memberId = AuthenticationUtil.getMemberId();

		Comment comment = getCommentById(commentId);

		verifyCommentDeleteAuthority(comment, memberId);

		commentRepository.delete(comment);
	}

	public void deleteCommentWithoutAuth(Long commentId) {
		commentRepository.deleteById(commentId);
	}

	private void verifyCommentDeleteAuthority(Comment comment, Long memberId) {
		if (comment.isNotWrittenBy(memberId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	public Comment getCommentById(Long commentId) {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	public List<CommentResponse> getComments(Long offerId) {
		return commentRepository.findByOfferId(offerId)
				.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

}
