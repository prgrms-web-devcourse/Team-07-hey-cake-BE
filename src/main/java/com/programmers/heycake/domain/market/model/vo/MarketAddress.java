package com.programmers.heycake.domain.market.model.vo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MarketAddress {

	@Column(name = "city", length = 10, nullable = false)
	private String city;

	@Column(name = "district", length = 10, nullable = false)
	private String district;

	@Column(name = "detailAddress", length = 80, nullable = false)
	private String detailAddress;

	public MarketAddress(String city, String district, String detailAddress) {
		this.city = city;
		this.district = district;
		this.detailAddress = detailAddress;
	}
}
