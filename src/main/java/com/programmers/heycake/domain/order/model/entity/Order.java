package com.programmers.heycake.domain.order.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.order.model.vo.OrderStatus;
import com.programmers.heycake.domain.thread.model.entity.Thread;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	@Column(name = "title", length = 20, nullable = false)
	private String title;

	@Column(name = "order_status", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(name = "hope_price", nullable = false)
	private Long hopePrice;

	@Column(name = "region", length = 20, nullable = false)
	private String region;

	@Column(name = "visit_date", nullable = false)
	private LocalDateTime visitDate;

	@Embedded
	private CakeInfo cakeInfo;

	@OneToMany(mappedBy = "order")
	private List<Thread> threads = new ArrayList<>();

	@Builder
	public Order(
			Long memberId, String title, OrderStatus orderStatus, Long hopePrice,
			String region, LocalDateTime visitDate, CakeInfo cakeInfo
	) {
		this.memberId = memberId;
		this.title = title;
		this.orderStatus = orderStatus;
		this.hopePrice = hopePrice;
		this.region = region;
		this.visitDate = visitDate;
		this.cakeInfo = cakeInfo;
	}
}
