package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Cart;

import java.util.List;
import java.util.UUID;

public interface ICartService {
    Cart getCartByUserId(UUID userId);
    void createCart(UUID userId);
    List<Cart> getAllCarts();
    void deleteCart(UUID cartId);
}
