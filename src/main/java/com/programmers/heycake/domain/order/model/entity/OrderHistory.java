package com.programmers.heycake.domain.order.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.programmers.heycake.domain.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "order_history")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE order_history SET deleted_at = NOW() WHERE id = ?")
public class OrderHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "market_id", nullable = false)
	private Long marketId;

	@Column(name = "sugar_score", nullable = true)
	private Integer sugarScore;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	public OrderHistory(Long memberId, Long marketId) {
		this.memberId = memberId;
		this.marketId = marketId;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void updateSugarScore(Integer sugarContent) {
		this.sugarScore = sugarContent;
	}
}
