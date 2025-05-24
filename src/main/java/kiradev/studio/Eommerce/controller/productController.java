package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.service.ProductService;
import kiradev.studio.Eommerce.service.ShopService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class productController {
    private final ProductService productService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public productController(ProductService productService, UserService userService, JwtUtil jwtUtil) {
        this.productService = productService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Validates the JWT token and extracts the email.
     *
     * @param token the JWT token
     * @return ResponseEntity with email if valid, or error message if invalid
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

    //write api get product by product id
    @GetMapping("/getProductById")
    public ResponseEntity<?> getProductById(@RequestParam UUID productId,
                                            @RequestHeader("Authorization") String token) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }
        if (productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ Product ID is required"));
        }

        var product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Product not found"));
        }

        return ResponseEntity.ok(Map.of(
                "state", "success",
                "data", product
        ));
    }

    //write api get all products check permission ADMIN
    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }

        var products = productService.getAllProducts();
        return ResponseEntity.ok(Map.of(
                "state", "success",
                "data", products
        ));
    }
}
