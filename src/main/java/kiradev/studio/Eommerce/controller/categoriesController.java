package kiradev.studio.Eommerce.controller;


import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.Category;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.CategoryService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/categories")
public class categoriesController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public categoriesController(CategoryService categoryService, UserService userService, JwtUtil jwtUtill) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.jwtUtil = jwtUtill;
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
     * Creates a new category.
     *
     * @param token       the JWT token for authentication
     * @param name        the name of the category
     * @param description a brief description of the category
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/createCategory")
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam String name,
            @RequestParam String description) {
        try {
            ResponseEntity<?> validation = validateToken(token);
            if (!validation.getStatusCode().is2xxSuccessful()) return validation;
            categoryService.createCategory(name, description);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Category created successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Updates an existing category.
     *
     * @param token       the JWT token for authentication
     * @param categoryId  the UUID of the category to update
     * @param name        the new name for the category (optional)
     * @param description the new description for the category (optional)
     * @return ResponseEntity with success or error message
     */
    @PutMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID categoryId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            categoryService.updateCategory(categoryId, name, description);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Category updated successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ An error occurred while updating the category"));
        }
    }

    /**
     * Deletes a category by its ID.
     *
     * @param token      the JWT token for authentication
     * @param categoryId the UUID of the category to delete
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/deleteCategory")
    public ResponseEntity<?> deleteCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID categoryId) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Category deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param token       the JWT token for authentication
     * @param name        the name of the category to retrieve
     * @return ResponseEntity with the category data or an error message
     */
    @GetMapping("/getCategoryByName")
    public ResponseEntity<?> getCategoryByName(
            @RequestHeader("Authorization") String token,
            @RequestParam String name) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            return ResponseEntity.ok(Map.of("data", categoryService.getCategoryByName(name)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ An error occurred while retrieving the category"));
        }
    }

    /**
     * Retrieves all categories.
     *
     * @param token the JWT token for authentication
     * @return ResponseEntity with the list of categories or an error message
     */
    @GetMapping("/getAllCategories")
    public ResponseEntity<?> getAllCategories(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        User user = userService.findByEmail((String) validation.getBody())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(user, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }

        try {
            return ResponseEntity.ok(Map.of("data", categoryService.getAllCategories()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ An error occurred while retrieving all categories"));
        }
    }

    /**
     * Retrieves products by category name.
     *
     * @param token        the JWT token for authentication
     * @param categoryName the name of the category to retrieve products from
     * @return ResponseEntity with the list of products in the specified category or an error message
     */
    @GetMapping("/getProductsByCategory")
    public ResponseEntity<?> getProductsByCategory(
            @RequestHeader("Authorization") String token,
            @RequestParam String categoryName) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {

            Category category = categoryService.getCategoryByName(categoryName);
            if (category==null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Category not found"));
            }else{
                return ResponseEntity.ok(Map.of("state", "fail", "data", categoryService.getProductsByCategoryName(categoryName)));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("state", "fail", "msg", "❌ An error occurred while retrieving products by category"));
        }
    }


}
