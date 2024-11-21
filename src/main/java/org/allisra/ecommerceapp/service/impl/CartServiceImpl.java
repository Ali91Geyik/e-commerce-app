package org.allisra.ecommerceapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allisra.ecommerceapp.exception.BadRequestException;
import org.allisra.ecommerceapp.exception.ResourceNotFoundException;
import org.allisra.ecommerceapp.mapper.CartMapper;
import org.allisra.ecommerceapp.mapper.ProductMapper;
import org.allisra.ecommerceapp.model.dto.cart.AddToCartDTO;
import org.allisra.ecommerceapp.model.dto.cart.CartDTO;
import org.allisra.ecommerceapp.model.dto.cart.UpdateCartItemDTO;
import org.allisra.ecommerceapp.model.dto.product.ProductDTO;
import org.allisra.ecommerceapp.model.entity.Cart;
import org.allisra.ecommerceapp.model.entity.CartItem;
import org.allisra.ecommerceapp.model.entity.Product;
import org.allisra.ecommerceapp.model.entity.User;
import org.allisra.ecommerceapp.repository.CartItemRepository;
import org.allisra.ecommerceapp.repository.CartRepository;
import org.allisra.ecommerceapp.service.CartService;
import org.allisra.ecommerceapp.service.ProductService;
import org.allisra.ecommerceapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final UserService userService;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartById(Long id) {
        Cart cart = findCartEntityById(id);
        if (!isCartBelongsToCurrentUser(cart.getId())) {
            throw new BadRequestException("You don't have permission to access this cart");
        }
        return cartMapper.entityToDto(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCurrentUserCart() {
        Cart cart = getOrCreateCurrentUserCart();
        return cartMapper.entityToDto(cart);
    }

    @Override
    public CartDTO addToCart(AddToCartDTO addToCartDTO) {
        Cart cart = getOrCreateCurrentUserCart();

        // DTO'yu alıyoruz
        ProductDTO productDTO = productService.getProductById(addToCartDTO.getProductId());

        // Product dönüşümünü yapıyoruz
        // Bu noktada category gibi ilişkiler gerekli olmadığı için basit dönüşüm yeterli
        Product product = productMapper.dtoToEntity(productDTO);

        // Dönüşümün doğru olduğundan emin oluyoruz
        if (product == null || product.getId() == null) {
            throw new BadRequestException("Invalid product conversion");
        }

        // Stok kontrolü
        if (product.getStockQuantity() < addToCartDTO.getQuantity()) {
            throw new BadRequestException("Not enough stock for product: " + product.getName());
        }

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setPrice(product.getPrice());
                    return newItem;
                });

        cartItem.setQuantity(addToCartDTO.getQuantity());
        cartItemRepository.save(cartItem);

        return cartMapper.entityToDto(cart);
    }

    @Override
    public CartDTO updateCartItem(UpdateCartItemDTO updateCartItemDTO) {
        CartItem cartItem = cartItemRepository.findById(updateCartItemDTO.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!isCartBelongsToCurrentUser(cartItem.getCart().getId())) {
            throw new BadRequestException("You don't have permission to modify this cart");
        }

        if (updateCartItemDTO.getQuantity() == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            // Stok kontrolü
            if (cartItem.getProduct().getStockQuantity() < updateCartItemDTO.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + cartItem.getProduct().getName());
            }
            cartItem.setQuantity(updateCartItemDTO.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return getCartById(cartItem.getCart().getId());
    }

    @Override
    public CartDTO removeFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!isCartBelongsToCurrentUser(cartItem.getCart().getId())) {
            throw new BadRequestException("You don't have permission to modify this cart");
        }

        Long cartId = cartItem.getCart().getId();
        cartItemRepository.delete(cartItem);

        return getCartById(cartId);
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        if (!isCartBelongsToCurrentUser(cart.getId())) {
            throw new BadRequestException("You don't have permission to modify this cart");
        }
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDTO abandonCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        cart.setStatus(Cart.CartStatus.ABANDONED);
        return cartMapper.entityToDto(cartRepository.save(cart));
    }

    @Override
    public CartDTO reactivateCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        cart.setStatus(Cart.CartStatus.ACTIVE);
        return cartMapper.entityToDto(cartRepository.save(cart));
    }

    @Override
    public CartDTO checkoutCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cannot checkout empty cart");
        }
        cart.setStatus(Cart.CartStatus.CHECKED_OUT);
        return cartMapper.entityToDto(cartRepository.save(cart));
    }

    @Override
    public void deleteCart(Long cartId) {
        Cart cart = findCartEntityById(cartId);
        if (!isCartBelongsToCurrentUser(cart.getId())) {
            throw new BadRequestException("You don't have permission to delete this cart");
        }
        cartRepository.delete(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCartBelongsToCurrentUser(Long cartId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Cart cart = findCartEntityById(cartId);
        return cart.getUser().getEmail().equals(currentUserEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public Cart findCartEntityById(Long cartId) {
        return cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));
    }

    @Override
    public Cart getOrCreateCurrentUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userService.findUserEntityByEmail(userEmail);

        return cartRepository.findByUserAndStatus(user, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }
}