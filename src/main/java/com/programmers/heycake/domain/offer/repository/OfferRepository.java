package com.programmers.heycake.domain.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.Order;

public interface OfferRepository extends JpaRepository<Offer, Long> {
	boolean existsByMarketIdAndOrder(Long marketId, Order order);
}
