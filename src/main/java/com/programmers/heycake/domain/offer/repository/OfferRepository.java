package com.programmers.heycake.domain.offer.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.programmers.heycake.domain.offer.model.entity.Offer;
import com.programmers.heycake.domain.order.model.entity.Order;

public interface OfferRepository extends JpaRepository<Offer, Long> {
	@Query(value = "SELECT o FROM Offer o JOIN fetch o.order WHERE o.id = :offerId ")
	Optional<Offer> findByIdWithFetchJoin(Long offerId);

	boolean existsByMarketIdAndOrder(Long marketId, Order order);

	@Query("SELECT DISTINCT o FROM Offer o LEFT JOIN FETCH o.comments WHERE o.order = :order")
	List<Offer> findAllByOrderFetchComments(@Param(value = "order") Order order);
}
