package com.programmers.heycake.domain.order.model.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.programmers.heycake.domain.order.model.vo.BreadFlavor;
import com.programmers.heycake.domain.order.model.vo.CakeCategory;
import com.programmers.heycake.domain.order.model.vo.CakeHeight;
import com.programmers.heycake.domain.order.model.vo.CakeSize;
import com.programmers.heycake.domain.order.model.vo.CreamFlavor;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CakeInfo {

	@Column(name = "cake_category", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private CakeCategory cakeCategory;

	@Column(name = "cake_size", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private CakeSize cakeSize;

	@Column(name = "cake_height", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private CakeHeight cakeHeight;

	@Column(name = "bread_flavor", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private BreadFlavor breadFlavor;

	@Column(name = "cream_flavor", length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private CreamFlavor creamFlavor;

	@Column(name = "requirements", length = 500, nullable = true)
	private String requirements;

	@Builder
	private CakeInfo(
			String cakeCategory, String cakeSize, String cakeHeight,
			String breadFlavor, String creamFlavor, String requirements
	) {
		this.cakeCategory = CakeCategory.valueOf(cakeCategory);
		this.cakeSize = CakeSize.valueOf(cakeSize);
		this.cakeHeight = CakeHeight.valueOf(cakeHeight);
		this.breadFlavor = BreadFlavor.valueOf(breadFlavor);
		this.creamFlavor = CreamFlavor.valueOf(creamFlavor);
		this.requirements = requirements;
	}
}
