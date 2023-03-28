package com.programmers.heycake.common.cache;

import java.time.Duration;

import lombok.Getter;

@Getter
public enum CacheType {
	OFFERS("offers"),
	COMMENTS("comments");

	private String name;
	private Duration expireAfterWrite;
	private int maximumSize;

	CacheType(String name) {
		this.name = name;
		this.expireAfterWrite = CacheConst.EXPIRE_MINUTES;
		this.maximumSize = CacheConst.MAXIMUM_SIZE;
	}
}
