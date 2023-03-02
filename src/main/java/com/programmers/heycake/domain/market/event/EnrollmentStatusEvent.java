package com.programmers.heycake.domain.market.event;

import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnrollmentStatusEvent {

	private final Long enrollmentId;
	private final EnrollmentStatus requestStatus;

	public boolean isApproved() {
		return this.requestStatus == EnrollmentStatus.APPROVED;
	}
}
