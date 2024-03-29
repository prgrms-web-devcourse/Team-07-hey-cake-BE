package com.programmers.heycake.domain.comment.model.entity;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.offer.model.entity.Offer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "comment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE comment SET deleted_at = NOW() WHERE id = ?")
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "content", length = 500, nullable = false)
	private String content;

	@Column(name = "parent_comment_id", nullable = true)
	private Long parentCommentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_id")
	private Offer offer;

	public Comment(Long memberId, String content, Long parentCommentId) {
		this.memberId = memberId;
		this.content = content;
		this.parentCommentId = parentCommentId;
	}

	public void setOffer(Offer offer) {
		if (Objects.nonNull(this.offer)) {
			this.offer.getComments().remove(this);
		}
		this.offer = offer;
		offer.getComments().add(this);
	}

	public boolean isNotWrittenBy(Long targetMemberId) {
		return !Objects.equals(this.memberId, targetMemberId);
	}

	public boolean isDeleted() {
		return this.getDeletedAt() != null;
	}
}
