package com.example.demo.cart.repository;

import com.example.demo.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query("SELECT DISTINCT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "LEFT JOIN FETCH ci.bread " +
           "LEFT JOIN FETCH c.account")
    List<Cart> findAllWithItemsAndBread();
    
    @Query("SELECT c FROM Cart c " +
           "LEFT JOIN FETCH c.cartItems ci " +
           "LEFT JOIN FETCH ci.bread " +
           "WHERE c.account.id = :accountId")
    Optional<Cart> findByAccountIdWithItems(@Param("accountId") Long accountId);
}
