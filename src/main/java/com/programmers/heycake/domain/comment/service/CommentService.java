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
import com.programmers.heycake.domain.market.repository.MarketRepository;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.repository.MemberRepository;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.repository.OfferRepository;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final OfferRepository offerRepository;
	private final MarketRepository marketRepository;
	private final MemberRepository memberRepository;

	public Long saveComment(String content, Long offerId, Long memberId) {
		Offer offer = getOffer(offerId);
		Order order = offer.getOrder();

		verifyCommentWriteAuthority(order, offer, memberId);

		Comment comment = toEntity(memberId, content);
		comment.setOffer(offer);

		commentRepository.save(comment);

		return comment.getId();
	}

	private void verifyCommentWriteAuthority(Order order, Offer offer, Long memberId) {
		Market market = getMarket(offer.getMarketId());
		Member member = getMember(memberId);

		if ((order.isNotWrittenBy(memberId)) && member.isDifferentMember(market.getMember())) {
			throw new BusinessException(ErrorCode.FORBIDDEN);
		}
	}

	private Offer getOffer(Long offerId) {
		return offerRepository.findByIdFetchOrder(offerId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private Market getMarket(Long marketId) {
		return marketRepository.findById(marketId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	private Member getMember(Long memberId) {
		return memberRepository.findById(memberId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	public void deleteComment(Long commentId) {
		Long memberId = AuthenticationUtil.getMemberId();

		Comment comment = getComment(commentId);

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

	private Comment getComment(Long commentId) {
		return commentRepository.findById(commentId)
				.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
	}

	public List<CommentResponse> getComments(Long offerId) {
		return commentRepository.findByOfferId(offerId)
				.stream()
				.map(CommentMapper::toCommentResponse)
				.toList();
	}

	public int countCommentsByOffer(Offer offer) {
		return commentRepository.countByOffer(offer);
	}
}
