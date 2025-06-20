package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.repository.CartRepository;
import kiradev.studio.Eommerce.service.Interface.ICartService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CartService implements ICartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    /**
     * Retrieves the cart associated with a specific user ID.
     *
     * @param userId the UUID of the user whose cart is to be retrieved
     * @return the Cart object associated with the user ID
     */
    @Override
    public Cart getCartByUserId(UUID userId) {
        return cartRepository.findByUserId(userId).get();
    }

    /**
     * Creates a new cart for a user with the specified user ID.
     * The cart is initialized with the current timestamp.
     *
     * @param userId the UUID of the user for whom the cart is to be created
     */
    @Override
    public void createCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCreatedAt(Instant.now().toString());
        cartRepository.save(cart);
    }

    /**
     * Retrieves all carts from the repository.
     *
     * @return a list of all Cart objects
     */
    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    /**
     * Deletes a cart identified by its UUID.
     *
     * @param cartId the UUID of the cart to be deleted
     */
    @Override
    public void deleteCart(UUID cartId) {
        cartRepository.deleteById(cartId);
    }


    /**
     * Checks if a cart exists for a specific user ID.
     *
     * @param userId the UUID of the user whose cart existence is to be checked
     * @return true if the cart exists, false otherwise
     */
    public boolean isCartExist(UUID userId) {
        return cartRepository.findByUserId(userId).isPresent();
    }
}
