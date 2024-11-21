package org.allisra.ecommerceapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.allisra.ecommerceapp.model.dto.cart.AddToCartDTO;
import org.allisra.ecommerceapp.model.dto.cart.CartDTO;
import org.allisra.ecommerceapp.model.dto.cart.UpdateCartItemDTO;
import org.allisra.ecommerceapp.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/current")
    public ResponseEntity<CartDTO> getCurrentUserCart() {
        return ResponseEntity.ok(cartService.getCurrentUserCart());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getCartById(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@Valid @RequestBody AddToCartDTO addToCartDTO) {
        CartDTO updatedCart = cartService.addToCart(addToCartDTO);
        return new ResponseEntity<>(updatedCart, HttpStatus.CREATED);
    }

    @PutMapping("/items")
    public ResponseEntity<CartDTO> updateCartItem(@Valid @RequestBody UpdateCartItemDTO updateCartItemDTO) {
        return ResponseEntity.ok(cartService.updateCartItem(updateCartItemDTO));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeFromCart(@PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.removeFromCart(cartItemId));
    }

    @PostMapping("/{id}/checkout")
    public ResponseEntity<CartDTO> checkoutCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.checkoutCart(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/items")
    public ResponseEntity<Void> clearCart(@PathVariable Long id) {
        cartService.clearCart(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/abandon")
    public ResponseEntity<CartDTO> abandonCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.abandonCart(id));
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<CartDTO> reactivateCart(@PathVariable Long id) {
        return ResponseEntity.ok(cartService.reactivateCart(id));
    }
}
