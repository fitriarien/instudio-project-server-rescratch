package com.fitriarien.instudio.repository;

import com.fitriarien.instudio.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT COUNT(*) FROM Order") Long getMaxOrder();
}
