package com.example.demo.cart.repository;

import com.example.demo.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @Query("SELECT ci FROM CartItem ci " +
           "JOIN FETCH ci.bread " +
           "JOIN FETCH ci.cart c " +
           "JOIN FETCH c.account " +
           "WHERE ci.id = :cartItemId")
    Optional<CartItem> findByIdWithBreadAndCart(@Param("cartItemId") Long cartItemId);
    
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END " +
           "FROM CartItem ci " +
           "WHERE ci.id = :cartItemId AND ci.cart.account.id = :accountId")
    boolean existsByIdAndAccountId(@Param("cartItemId") Long cartItemId, @Param("accountId") Long accountId);
}