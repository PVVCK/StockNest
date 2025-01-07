package com.ecommerce.stocknest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.stocknest.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

}
