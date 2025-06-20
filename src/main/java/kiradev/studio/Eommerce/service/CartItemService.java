package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.entity.CartItem;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.repository.CartItemRepository;
import kiradev.studio.Eommerce.repository.ProductRepository;
import kiradev.studio.Eommerce.service.Interface.ICartItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartItemService implements ICartItemService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public CartItemService(ProductRepository productRepository, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * Adds an item to the cart.
     *
     * @param cart     The cart to which the item will be added.
     * @param products The product to be added to the cart.
     * @param quantity The quantity of the product to be added.
     */
    @Override
    public void addItemToCart(Cart cart, Products products, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(products);
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
    }

    /**
     * Updates the quantity of an item in the cart.
     *
     * @param cart     The cart containing the item.
     * @param product  The product whose quantity is to be updated.
     * @param quantity The new quantity to set for the product.
     */
    @Override
    public void updateItemQuantity(Cart cart, Products product, int quantity) {
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).get();
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        if(cartItem.getQuantity() <= 0){
            cartItemRepository.delete(cartItem);
            return;
        }
        cartItemRepository.save(cartItem);
    }

    /**
     * Removes an item from the cart.
     *
     * @param cart    The cart from which the item will be removed.
     * @param product The product to be removed from the cart.
     */
    @Override
    public void removeItemFromCart(Cart cart, Products product) {
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product).get();
        cartItemRepository.delete(cartItem);
    }

    /**
     * Clears all items from the cart.
     *
     * @param cart The cart to be cleared.
     */
    @Override
    public void clearCart(Cart cart) {
        cartItemRepository.deleteByCart(cart);
    }

    /**
     * Retrieves all items in the cart.
     *
     * @param cart The cart for which items are to be retrieved.
     * @return A list of CartItem objects in the specified cart.
     */
    @Override
    public List<CartItem> getCartItemsByCart(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    /**
     * Checks if a product is already in the cart.
     *
     * @param cart     The cart to check.
     * @param products The product to check for.
     * @return true if the product is in the cart, false otherwise.
     */
    public boolean checkProductInCart(Cart cart, Products products) {
        return cartItemRepository.findByCartAndProduct(cart, products).isPresent();
    }
}
