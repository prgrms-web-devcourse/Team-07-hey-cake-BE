package com.programmers.heycake.domain.offer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.Order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.programmers.heycake.domain.offer.model.entity.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {
	@Query(value = "SELECT o FROM Offer o JOIN fetch o.order WHERE o.id = :offerId ")
	Optional<Offer> findById(Long offerId);

	boolean existsByMarketIdAndOrder(Long marketId, Order order);
}
