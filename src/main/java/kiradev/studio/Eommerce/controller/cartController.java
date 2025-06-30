package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.Cart;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.CartService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class cartController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CartService cartService;

    public cartController(UserService userService, JwtUtil jwtUtil, CartService cartService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.cartService = cartService;
    }

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

    //write doumentation for this method getCart
    /**
     * Retrieves the cart for the authenticated user.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the cart data or an error message.
     */
    @GetMapping("/getCart")
    public ResponseEntity<?> getCart(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(cartService.isCartExist(user.getID())){
            try {
                return ResponseEntity.ok(Map.of("state", "success", "data", cartService.getCartByUserId(user.getID())));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart isn't exits for user"));
        }
    }

    /**
     * Creates a new cart for the authenticated user.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity indicating the success or failure of the cart creation.
     */
    @PutMapping("/createCart")
    public ResponseEntity<?> createCart(@RequestHeader("Authorization") String token){
        ResponseEntity<?> validation = validateToken(token);
        if(!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(cartService.isCartExist(user.getID())){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Cart already exists for user"));
        } else {
            cartService.createCart(user.getID());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("state", "success", "msg", "✅ Cart created successfully"));
        }
    }

    /**
     * Deletes the cart for the authenticated user.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity indicating the success or failure of the cart deletion.
     */
    @GetMapping("/getAllCarts")
    public ResponseEntity<?> getAllCarts(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(user, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }else{
            try {
                return ResponseEntity.ok(Map.of("state", "success", "data", cartService.getAllCarts()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
            }
        }
    }

    /**
     * Deletes the cart for a specific user by email.
     *
     * @param token The JWT token for authentication.
     * @param mail  The email of the user whose cart is to be deleted.
     * @return A ResponseEntity indicating the success or failure of the cart deletion.
     */
    @DeleteMapping("/deteleCarts")
    public ResponseEntity<?> deleteCarts(
            @RequestHeader("Authorization") String token,
            @RequestParam String mail){
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        } else {
            try {
                User user = userService.findByEmail(mail).orElseThrow(() -> new RuntimeException("User not found"));
                Cart cart = cartService.getCartByUserId(user.getID());
                cartService.deleteCart(cart.getId());
                return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Cart deleted successfully"));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
            }
        }
    }
}
