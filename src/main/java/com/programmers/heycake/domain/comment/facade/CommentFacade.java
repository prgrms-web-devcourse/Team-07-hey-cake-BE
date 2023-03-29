package com.programmers.heycake.domain.comment.facade;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.mapper.CommentMapper;
import com.programmers.heycake.common.util.AuthenticationUtil;
import com.programmers.heycake.domain.comment.model.dto.request.CommentCreateRequest;
import com.programmers.heycake.domain.comment.model.dto.response.ChildCommentsResponse;
import com.programmers.heycake.domain.comment.model.dto.response.CommentsResponse;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.comment.service.CommentService;
import com.programmers.heycake.domain.image.model.dto.ImageResponse;
import com.programmers.heycake.domain.image.model.dto.ImageResponses;
import com.programmers.heycake.domain.image.model.vo.ImageType;
import com.programmers.heycake.domain.image.service.ImageService;
import com.programmers.heycake.domain.market.model.entity.Market;
import com.programmers.heycake.domain.market.service.MarketService;
import com.programmers.heycake.domain.member.model.entity.Member;
import com.programmers.heycake.domain.member.service.MemberService;
import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.offer.service.OfferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentFacade {

	private static final String COMMENT_SUB_PATH = "images/comments";

	private final MemberService memberService;
	private final CommentService commentService;
	private final MarketService marketService;
	private final OfferService offerService;
	private final ImageService imageService;

	@CacheEvict(cacheNames = "comments", allEntries = true)
	@Transactional
	public void deleteComment(Long commentId) {
		List<ImageResponse> commentImageResponse = imageService.getImages(commentId, ImageType.COMMENT).images();
		if (!commentImageResponse.isEmpty()) {
			imageService.deleteImages(commentId, ImageType.COMMENT, COMMENT_SUB_PATH);
		}

		commentService.deleteComment(commentId);
	}

	@Transactional
	public void deleteCommentWithoutAuth(Long commentId) {
		List<ImageResponse> commentImageResponse = imageService.getImages(commentId, ImageType.COMMENT).images();
		if (!commentImageResponse.isEmpty()) {
			imageService.deleteImages(commentId, ImageType.COMMENT, COMMENT_SUB_PATH);
		}

		commentService.deleteCommentWithoutAuth(commentId);
	}

	@CacheEvict(cacheNames = "comments", key = "#commentCreateRequest.offerId")
	@Transactional
	public Long createComment(CommentCreateRequest commentCreateRequest) {
		Long memberId = AuthenticationUtil.getMemberId();

		Offer offer = offerService.getOfferWithOrderById(commentCreateRequest.offerId());
		Market market = marketService.getMarketById(offer.getMarketId());
		Member member = memberService.getMemberById(memberId);

		Long createdCommentId = commentService.createComment(
				commentCreateRequest.content(),
				commentCreateRequest.depth(),
				commentCreateRequest.parentCommentId(),
				offer,
				market,
				member
		);

		if (commentCreateRequest.existsImage()) {
			imageService.createAndUploadImage(
					commentCreateRequest.image(),
					COMMENT_SUB_PATH,
					createdCommentId,
					ImageType.COMMENT
			);
		}

		return createdCommentId;
	}

	@Cacheable(cacheNames = "comments", key = "#offerId")
	@Transactional(readOnly = true)
	public List<CommentsResponse> getComments(Long offerId) {
		List<Comment> comments = commentService.getCommentsByOfferId(offerId);
		return comments.stream()
				.map(
						comment -> {
							Member member = memberService.getMemberById(comment.getMemberId());
							int numberOfChildComment = commentService.countChildComment(comment.getId());
							ImageResponses imageResponses = imageService.getImages(comment.getId(), ImageType.COMMENT);
							return CommentMapper.toCommentsResponse(comment, member, imageResponses, numberOfChildComment);
						}
				).toList();
	}

	@Transactional(readOnly = true)
	public List<ChildCommentsResponse> getChildComments(Long commentId) {
		List<Comment> childComments = commentService.getChildCommentsById(commentId);
		return childComments.stream()
				.map(
						comment -> {
							Member member = memberService.getMemberById(comment.getMemberId());
							ImageResponses imageResponses = imageService.getImages(comment.getId(), ImageType.COMMENT);
							return CommentMapper.toChildCommentResponse(comment, member, imageResponses);
						}
				).toList();
	}
}
