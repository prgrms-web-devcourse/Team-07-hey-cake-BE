package com.programmers.heycake.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.order.model.entity.OrderHistory;

public interface HistoryRepository extends JpaRepository<OrderHistory, Long> {
}
