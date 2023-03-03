package com.programmers.heycake.domain.market.model.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.programmers.heycake.domain.BaseEntity;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;
import com.programmers.heycake.domain.market.model.vo.MarketAddress;
import com.programmers.heycake.domain.member.model.entity.Member;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "market_enrollment")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE market_enrollment SET deleted_at = NOW() WHERE id = ?")
public class MarketEnrollment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "business_number", length = 10, nullable = false, unique = true)
	private String businessNumber;

	@Column(name = "owner_name", length = 10, nullable = false)
	private String ownerName;

	@Column(name = "open_date", nullable = false)
	private LocalDate openDate;

	@Column(name = "market_name", length = 20, nullable = false)
	private String marketName;

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

	@Column(name = "enrollment_status", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private EnrollmentStatus enrollmentStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public MarketEnrollment(
			String businessNumber,
			String ownerName,
			LocalDate openDate,
			String marketName,
			String phoneNumber,
			MarketAddress marketAddress,
			LocalTime openTime,
			LocalTime endTime,
			String description
	) {
		this.businessNumber = businessNumber;
		this.ownerName = ownerName;
		this.openDate = openDate;
		this.marketName = marketName;
		this.phoneNumber = phoneNumber;
		this.marketAddress = marketAddress;
		this.openTime = openTime;
		this.endTime = endTime;
		this.description = description;
		this.enrollmentStatus = EnrollmentStatus.WAITING;
	}

	public boolean isSameStatus(EnrollmentStatus enrollmentStatus) {
		return this.enrollmentStatus == enrollmentStatus;
	}

	public void updateEnrollmentStatus(EnrollmentStatus enrollmentStatus) {
		this.enrollmentStatus = enrollmentStatus;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
