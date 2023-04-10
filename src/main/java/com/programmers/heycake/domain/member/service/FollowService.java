package com.programmers.heycake.domain.member.service;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.member.model.entity.Follow;
import com.programmers.heycake.domain.member.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;

	@Transactional
	public Long createFollow(Long marketId, Long memberId) {
		if (followRepository.existsByMemberIdAndMarketId(memberId, marketId)) {
			throw new BusinessException(ErrorCode.DUPLICATED);
		}

		Follow follow = Follow.builder()
				.memberId(memberId)
				.marketId(marketId)
				.build();

		return followRepository.save(follow).getId();
	}

	@Transactional
	public void deleteFollow(Long marketId) {
		if (!followRepository.existsByMemberIdAndMarketId(getMemberId(), marketId)) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}

		followRepository.deleteByMemberIdAndMarketId(getMemberId(), marketId);
	}
}
