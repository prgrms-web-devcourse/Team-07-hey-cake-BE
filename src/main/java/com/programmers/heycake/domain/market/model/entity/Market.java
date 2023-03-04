package com.programmers.heycake.domain.market.model.entity;

import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
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
import com.programmers.heycake.domain.market.model.vo.MarketAddress;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "market")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE market SET deleted_at = NOW() WHERE id = ?")
public class Market extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "phone_number", length = 20, nullable = false)
	private String phoneNumber;

	@Embedded
	private MarketAddress marketAddress;

	@Column(name = "open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "end_time", nullable = false)
	private LocalTime endTime;

	@Column(name = "description", length = 500, nullable = false)
	private String description;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "market_enrollment_id")
	private MarketEnrollment marketEnrollment;

	@Builder
	public Market(
			String phoneNumber,
			MarketAddress marketAddress,
			LocalTime openTime,
			LocalTime endTime,
			String description
	) {
		this.phoneNumber = phoneNumber;
		this.marketAddress = marketAddress;
		this.openTime = openTime;
		this.endTime = endTime;
		this.description = description;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void setMarketEnrollment(MarketEnrollment marketEnrollment) {
		this.marketEnrollment = marketEnrollment;
	}

	public boolean isNotMarketMember(Long memberId) {
		return !Objects.equals(this.member.getId(), memberId);
	}

}
