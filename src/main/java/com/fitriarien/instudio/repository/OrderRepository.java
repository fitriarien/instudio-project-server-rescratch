package com.fitriarien.instudio.repository;

import com.fitriarien.instudio.entity.Order;
import com.fitriarien.instudio.entity.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    @Query("SELECT COUNT(*) FROM Order") Long getMaxOrder();
    List<Order> findByUserId(String userId);
}
