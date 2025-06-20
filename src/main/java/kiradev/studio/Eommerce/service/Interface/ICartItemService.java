package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.entity.CartItem;
import kiradev.studio.Eommerce.entity.Products;

import java.util.List;
import java.util.UUID;

public interface ICartItemService {
    void addItemToCart(Cart cart, Products product, int quantity);
    void updateItemQuantity(Cart cart, Products product, int quantity);
    void removeItemFromCart(Cart cart, Products product);
    void clearCart(Cart cart);
    List<CartItem> getCartItemsByCart(Cart cart);
}
