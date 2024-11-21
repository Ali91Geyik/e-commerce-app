package org.allisra.ecommerceapp.repository;

import org.allisra.ecommerceapp.model.entity.Cart;
import org.allisra.ecommerceapp.model.entity.CartItem;
import org.allisra.ecommerceapp.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    boolean existsByCartAndProduct(Cart cart, Product product);

    void deleteByCartAndProduct(Cart cart, Product product);
}
