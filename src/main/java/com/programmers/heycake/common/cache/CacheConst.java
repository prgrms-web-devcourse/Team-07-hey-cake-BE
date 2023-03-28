package com.programmers.heycake.common.cache;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheConst {
	public static final int MAXIMUM_SIZE = 10000;
	public static final Duration EXPIRE_MINUTES = Duration.ofMinutes(10);
}
