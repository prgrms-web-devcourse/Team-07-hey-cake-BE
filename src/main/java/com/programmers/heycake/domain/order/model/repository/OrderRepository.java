package com.programmers.heycake.domain.order.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.programmers.heycake.domain.order.model.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
