package com.programmers.heycake.domain.market.model.dto;

import com.programmers.heycake.common.validator.Enum;
import com.programmers.heycake.domain.market.model.vo.EnrollmentStatus;

public record MarketEnrollmentStatusRequest(@Enum(target = EnrollmentStatus.class) String status) {
}
