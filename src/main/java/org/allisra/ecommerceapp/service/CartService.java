package org.allisra.ecommerceapp.service;
import org.allisra.ecommerceapp.model.dto.cart.AddToCartDTO;
import org.allisra.ecommerceapp.model.dto.cart.CartDTO;
import org.allisra.ecommerceapp.model.dto.cart.UpdateCartItemDTO;
import org.allisra.ecommerceapp.model.entity.Cart;
public interface CartService {

    // Temel CRUD operasyonları
    CartDTO getCartById(Long id);
    CartDTO getCurrentUserCart();
    void clearCart(Long cartId);
    void deleteCart(Long cartId);

    // Sepet işlemleri
    CartDTO addToCart(AddToCartDTO addToCartDTO);
    CartDTO updateCartItem(UpdateCartItemDTO updateCartItemDTO);
    CartDTO removeFromCart(Long cartItemId);

    // Sepet durumu işlemleri
    CartDTO abandonCart(Long cartId);
    CartDTO reactivateCart(Long cartId);
    CartDTO checkoutCart(Long cartId);

    // Yardımcı metodlar
    boolean isCartBelongsToCurrentUser(Long cartId);
    Cart findCartEntityById(Long cartId);
    Cart getOrCreateCurrentUserCart();
}
