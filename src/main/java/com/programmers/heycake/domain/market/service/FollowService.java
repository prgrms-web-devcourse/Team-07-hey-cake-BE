package com.programmers.heycake.domain.market.service;

import static com.programmers.heycake.common.util.AuthenticationUtil.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.programmers.heycake.common.exception.BusinessException;
import com.programmers.heycake.common.exception.ErrorCode;
import com.programmers.heycake.domain.market.model.dto.request.FollowMarketRequest;
import com.programmers.heycake.domain.market.model.entity.Follow;
import com.programmers.heycake.domain.market.repository.FollowQueryDslRepository;
import com.programmers.heycake.domain.market.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final FollowQueryDslRepository followQueryDslRepository;

	@Transactional
	public Long createFollow(Long marketId, Long memberId) {
		if (isFollowed(memberId, marketId)) {
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
		if (!isFollowed(getMemberId(), marketId)) {
			throw new BusinessException(ErrorCode.BAD_REQUEST);
		}

		followRepository.deleteByMemberIdAndMarketId(getMemberId(), marketId);
	}

	@Transactional(readOnly = true)
	public List<Long> getFollowMarketIds(FollowMarketRequest followMarketRequest, Long memberId) {

		return followQueryDslRepository.getFollowMarketIds(followMarketRequest, memberId);
	}

	@Transactional(readOnly = true)
	public int getFollowNumber(Long marketId) {
		return followRepository.countByMarketId(marketId);
	}

	@Transactional(readOnly = true)
	public boolean isFollowed(Long memberId, Long marketId) {
		return followRepository.existsByMemberIdAndMarketId(memberId, marketId);
	}

}
