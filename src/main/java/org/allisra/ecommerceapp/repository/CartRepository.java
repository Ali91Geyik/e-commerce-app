package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Cart;
import org.allisra.ecommerceapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndStatus(User user, Cart.CartStatus status);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.product WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartItems ci LEFT JOIN FETCH ci.product WHERE c.user.id = :userId AND c.status = :status")
    Optional<Cart> findByUserIdAndStatusWithItems(@Param("userId") Long userId, @Param("status") Cart.CartStatus status);

    boolean existsByUserAndStatus(User user, Cart.CartStatus status);
}
