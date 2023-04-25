package com.programmers.heycake.domain.market.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.programmers.heycake.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "follow")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE follow SET deleted_at = NOW() WHERE id = ?")
public class Follow extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "market_id", nullable = false)
	private Long marketId;

	@Builder
	public Follow(Long memberId, Long marketId) {
		this.memberId = memberId;
		this.marketId = marketId;
	}
}
