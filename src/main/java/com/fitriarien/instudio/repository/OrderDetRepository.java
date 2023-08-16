package com.fitriarien.instudio.repository;

import com.fitriarien.instudio.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetRepository extends JpaRepository<OrderDetail, String> {
}
