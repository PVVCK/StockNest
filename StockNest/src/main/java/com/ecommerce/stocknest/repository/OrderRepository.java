package com.ecommerce.stocknest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.stocknest.model.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

}
