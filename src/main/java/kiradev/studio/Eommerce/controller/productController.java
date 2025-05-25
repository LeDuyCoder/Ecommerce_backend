package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.ProductStatus;
import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.dto.ProductDTO;
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
    private final ShopService shopService;
    private final JwtUtil jwtUtil;

    public productController(ProductService productService, UserService userService, ShopService shopService, JwtUtil jwtUtil) {
        this.productService = productService;
        this.userService = userService;
        this.shopService = shopService;
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

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the UUID of the product to retrieve
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the product is found.
     *                 <br>Body: {"state": "success", "data": product}</li>
     *             <li><b>400 Bad Request</b> – If the product ID is not provided.
     *                 <br>Body: {"state": "fail", "msg": "❌ Product ID is required"}</li>
     *             <li><b>404 Not Found</b> – If the product does not exist.
     *                 <br>Body: {"state": "fail", "msg": "❌ Product not found"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
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

    /**
     * Retrieves all products.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If products are found.
     *                 <br>Body: {"state": "success", "data": products}</li>
     *             <li><b>403 Forbidden</b> – If the user does not have permission to access this resource.
     *                 <br>Body: {"state": "fail", "msg": "❌ You do not have permission to access this resource"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
    @GetMapping("/getAllProducts")
    public ResponseEntity<?> getAllProducts(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }

        var products = productService.getAllProducts();
        return ResponseEntity.ok(Map.of(
                "state", "success",
                "data", products
        ));
    }

    /**
     * Retrieves all products by shop ID.
     *
     * @param productDTO the DTO containing the data shop
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If products are found.
     *                 <br>Body: {"state": "success", "data": products}</li>
     *             <li><b>400 Bad Request</b> – If the shop ID is not provided.
     *                 <br>Body: {"state": "fail", "msg": "❌ Shop ID is required"}</li>
     *             <li><b>403 Forbidden</b> – If the user does not have permission to access this resource.
     *                 <br>Body: {"state": "fail", "msg": "❌ You do not have permission to access this resource"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
    @PostMapping("/createProduct")
    public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String token,
                                           @RequestParam ProductDTO productDTO) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        var shop = shopService.getShopsByOwner(user.getID());

        if (!userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to create a product"));
        }

        productService.createProduct(productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getStock(), null, user.getID());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("state", "success", "msg", "✅ Product created successfully"));
    }

    /**
     * Updates an existing product.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param productId the UUID of the product to update
     * @param productDTO the DTO containing the updated product data
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the product is updated successfully.
     *                 <br>Body: {"state": "success", "msg": "✅ Product updated successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the product ID is not provided or invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Product ID is required"}</li>
     *             <li><b>403 Forbidden</b> – If the user does not have permission to update a product.
     *                 <br>Body: {"state": "fail", "msg": "❌ You do not have permission to update a product"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(@RequestHeader("Authorization") String token,
                                           @RequestParam UUID productId,
                                           @RequestBody ProductDTO productDTO) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to update a product"));
        }

        productService.updateProduct(productId, productDTO.getName(), productDTO.getDescription(), productDTO.getPrice(), productDTO.getStock(), null);
        return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Product updated successfully"));
    }

    /**
     * Deletes a product by its ID.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param productId the UUID of the product to delete
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the product is deleted successfully.
     *                 <br>Body: {"state": "success", "msg": "✅ Product deleted successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the product ID is not provided or invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Product ID is required"}</li>
     *             <li><b>403 Forbidden</b> – If the user does not have permission to delete a product.
     *                 <br>Body: {"state": "fail", "msg": "❌ You do not have permission to delete a product"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String token,
                                           @RequestParam UUID productId) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to delete a product"));
        }

        productService.deleteProduct(productId);
        return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Product deleted successfully"));
    }


    /**
     * Updates the status of a product.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param productId the UUID of the product to update
     * @param status the new status to set for the product, it is an enum of type ProductStatus: AVAILABLE, UNAVAILABLE, or DISCONTINUED,
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the product status is updated successfully.
     *                 <br>Body: {"state": "success", "msg": "✅ Product status updated successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the product ID or status is not provided.
     *                 <br>Body: {"state": "fail", "msg": "❌ Product ID and status are required"}</li>
     *             <li><b>403 Forbidden</b> – If the user does not have permission to update product status.
     *                 <br>Body: {"state": "fail", "msg": "❌ You do not have permission to update product status"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"state": "fail", "msg": "❌ Token validation failed"}</li>
     *         </ul>
     */
    @PutMapping("/updateProductStatus")
    public ResponseEntity<?> updateProductStatus(@RequestHeader("Authorization") String token,
                                                 @RequestParam UUID productId,
                                                 @RequestParam ProductStatus status) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        String email = (String) validation.getBody();
        var user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.hasPermission(user, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to update product status"));
        }

        productService.updateProductStatus(productId, status);
        return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Product status updated successfully"));
    }
}