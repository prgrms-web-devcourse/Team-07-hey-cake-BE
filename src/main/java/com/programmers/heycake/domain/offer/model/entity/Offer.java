package com.programmers.heycake.domain.offer.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "offer")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE offer SET deleted_at = NOW() WHERE id = ?")
public class Offer extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "market_id", nullable = false)
	private Long marketId;

	@Column(name = "expected_price", nullable = false)
	private int expectedPrice;

	@Column(name = "content", length = 500, nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@OneToMany(mappedBy = "offer")
	private List<Comment> comments = new ArrayList<>();

	public Offer(Long marketId, int expectedPrice, String content) {
		this.marketId = marketId;
		this.expectedPrice = expectedPrice;
		this.content = content;
	}

	public void setOrder(Order order) {
		if (Objects.nonNull(this.order)) {
			this.order.getOffers().remove(this);
		}
		this.order = order;
		order.getOffers().add(this);
	}

	public boolean identifyAuthor(Long marketId) {
		return Objects.equals(this.marketId, marketId);
	}
}
