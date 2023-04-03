package com.programmers.heycake.domain.comment.service;

import static com.programmers.heycake.common.mapper.CommentMapper.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.common.util.AuthenticationUtil;
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

	public Long createComment(
			String content,
			Long parentCommentId,
			Offer offer,
			Market market,
			Member member
	) {
		Order order = offer.getOrder();

		verifyOrderExpired(order);
		verifyCommentWriteAuthority(order, market, member);
		if (parentCommentId != null) {
			verifyCommentParentExists(parentCommentId);
		}

		Comment comment = toEntity(member.getId(), content, parentCommentId);

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

	private void verifyCommentParentExists(Long parentCommentId) {
		if (!commentRepository.existsById(parentCommentId)) {
			throw new BusinessException(ErrorCode.ENTITY_NOT_FOUND);
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

	public List<Comment> getParentCommentsByOfferId(Long offerId) {
		return commentRepository.findAllByOfferIdAndParentCommentIdIsNull(offerId);
	}

	public List<Comment> getChildCommentsById(Long parentCommentId) {
		return commentRepository.findAllByParentCommentId(parentCommentId);
	}

	public int countCommentsByOffer(Offer offer) {
		return commentRepository.countByOffer(offer);
	}

	public int countChildComment(Long parentCommentId) {
		return commentRepository.countByParentCommentId(parentCommentId);
	}
}
