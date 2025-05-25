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


    //write api create category
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

    //write api update category
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

    //write api delete category
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

    //write api get category by name
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

    //write api get category all
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

    //write api get products by category
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
