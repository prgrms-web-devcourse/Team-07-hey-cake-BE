package com.programmers.heycake.domain.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.offer.model.entity.Offer;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByOfferIdAndDepth(Long offerId, int depth);

	List<Comment> findAllByParentCommentId(Long parentCommentId);

	int countByOffer(Offer offer);

	int countByParentCommentId(Long parentCommentId);
}