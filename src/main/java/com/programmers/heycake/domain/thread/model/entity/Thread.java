package com.programmers.heycake.domain.thread.model.entity;

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

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.comment.model.entity.Comment;
import com.programmers.heycake.domain.order.model.entity.Order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "thread")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Thread extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long marketId;

	@Column(name = "expected_price", nullable = false)
	private int expectedPrice;

	@Column(name = "content", length = 500, nullable = false)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", referencedColumnName = "id")
	private Order order;

	@OneToMany(mappedBy = "thread")
	private List<Comment> comments = new ArrayList<>();

	public Thread(Long marketId, int expectedPrice, String content) {
		this.marketId = marketId;
		this.expectedPrice = expectedPrice;
		this.content = content;
	}

	public void setOrder(Order order) {
		if (Objects.nonNull(this.order)) {
			this.order.getThreads().remove(this);
		}
		this.order = order;
		order.getThreads().add(this);
	}
}
