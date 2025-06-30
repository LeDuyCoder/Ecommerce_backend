package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.dto.AddCartItemRequestDTO;
import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.CartItemService;
import kiradev.studio.Eommerce.service.CartService;
import kiradev.studio.Eommerce.service.ProductService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cartItems")
public class cartItemController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final ProductService productService;

    public cartItemController(UserService userService, JwtUtil jwtUtil, CartService cartService, CartItemService cartItemService, ProductService productService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    /**
     * Validates the JWT token and extracts the user's email.
     *
     * @param token The JWT token from the request header.
     * @return A ResponseEntity containing the user's email or an error message.
     */
    private ResponseEntity<?> validateToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("state", "fail", "msg", "❌ Missing or invalid token"));
        }

        String jwt = token.substring(7);
        String email = jwtUtil.extractEmail(jwt);

        if (!jwtUtil.validateToken(jwt, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("state", "fail", "msg", "❌ Token validation failed"));
        }

        return ResponseEntity.ok(email);
    }

    /**
     * Adds an item to the user's cart.
     *
     * @param token   The JWT token for authentication.
     * @param request The request body containing product ID and quantity.
     * @return A ResponseEntity indicating success or failure.
     */
    @PutMapping("/addCartItem")
    public ResponseEntity<?> addCartItem(
            @RequestHeader("Authorization") String token,
            @RequestBody AddCartItemRequestDTO request) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!cartService.isCartExist(user.getID())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart does not exist for user"));
        }

        // Lấy entity từ ID
        Cart cart = cartService.getCartByUserId(user.getID());
        Products product = productService.getProductById(request.getProductId());

        try {
            if (cartItemService.checkProductInCart(cart, product)) {
                cartItemService.updateItemQuantity(cart, product, request.getQuantity());
            } else {
                cartItemService.addItemToCart(cart, product, request.getQuantity());
            }

            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Item added to cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves the cart for the authenticated user.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the cart data or an error message.
     */
    @GetMapping("/getCartItems")
    public ResponseEntity<?> getCartItems(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!cartService.isCartExist(user.getID())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart does not exist for user"));
        }

        Cart cart = cartService.getCartByUserId(user.getID());
        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", cartItemService.getCartItemsByCart(cart)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Removes an item from the user's cart.
     *
     * @param token     The JWT token for authentication.
     * @param productId The ID of the product to remove.
     * @return A ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/removeCartItem")
    public ResponseEntity<?> removeCartItem(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID productId) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!cartService.isCartExist(user.getID())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart does not exist for user"));
        }

        Cart cart = cartService.getCartByUserId(user.getID());
        Products product = productService.getProductById(productId);

        try {
            cartItemService.removeItemFromCart(cart, product);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Item removed from cart successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Clears the user's cart.
     *
     * @param token The JWT token for authentication.
     * @param cart  The cart to clear.
     * @return A ResponseEntity indicating success or failure.
     */
    @DeleteMapping("/clearCart")
    public ResponseEntity<?> clearCart(
            @RequestHeader("Authorization") String token,
            Cart cart) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!cartService.isCartExist(user.getID())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart does not exist for user"));
        }

        try {
            cartItemService.clearCart(cart);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

}
