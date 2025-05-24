package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class userController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public userController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Validates the JWT token and extracts the email from it.
     *
     * @param token the JWT token
     * @return ResponseEntity with the email if valid, or an error message if invalid
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
     * Registers a new user with the provided email and password.
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @return ResponseEntity with the registration result
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String email, @RequestParam String password) {
        try {
            if (!userService.isMailExist(email)) {
                User newUser = userService.registerUser(password, email);
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", "null", "data", newUser));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ Email already exists"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ Register failed"));
        }
    }

    /**
     * Logs in a user using their email or username and password.
     *
     * @param email    the email of the user
     * @param username the username of the user
     * @param password the password of the user
     * @return ResponseEntity with the login result
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam(required = false) String email,
                                       @RequestParam(required = false) String username,
                                       @RequestParam String password) {
        if (email == null && username == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ Email or username is required"));
        }

        try {
            User user = userService.loginUser(email == null ? username : email, password);
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token, "data", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("msg", "❌ Login failed"));
        }
    }

    /**
     * Updates the user information.
     *
     * @param token       the JWT token
     * @param userDetails the new user details
     * @return ResponseEntity with the update result
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        @RequestBody User userDetails) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User updatedUser = userService.updateUser(email, userDetails);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Update succeeded", "data", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ Update failed"));
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param token the JWT token
     * @param id    the ID of the user to delete
     * @return ResponseEntity with the deletion result
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token,
                                        @RequestBody UUID id) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User user = userService.findByEmail(email).orElseThrow();
            if (userService.hasPermission(user, UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("state", "fail", "msg", "❌ You do not have permission to delete users"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ Error deleting user"));
        }
    }

    /**
     * Retrieves a user by email.
     *
     * @param token the JWT token
     * @param mail  the email of the user to retrieve
     * @return ResponseEntity with the user data
     */
    @GetMapping("/getUser")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String token,
                                     @RequestParam String mail) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            User user = userService.findByEmail(mail).orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(Map.of("data", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ User not found"));
        }
    }

    /**
     * Retrieves all users.
     *
     * @param token the JWT token
     * @return ResponseEntity with the list of users
     */
    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            if (userService.hasPermission(userAdmin, UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access"));
            }

            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ User not found"));
        }
    }

    /**
     * Retrieves all users with pagination and sorting.
     *
     * @param token the JWT token
     * @param page  the page number
     * @param size  the page size
     * @param sort  the sorting criteria
     * @return ResponseEntity with the paginated and sorted list of users
     */
    @GetMapping("/getAllUserPage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUserPage(@RequestHeader("Authorization") String token,
                                            @RequestParam int page,
                                            @RequestParam int size,
                                            @RequestParam String sort) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

            if (userService.hasPermission(userAdmin, UserRole.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access"));
            }

            return ResponseEntity.ok(userService.getAllUsersPage(page, size, sort));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ User not found"));
        }
    }

    /**
     * Updates the user's image.
     *
     * @param token      the JWT token
     * @param imageFile  the image file to update
     * @return ResponseEntity with the update result
     */
    @PutMapping(value = "/updateImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImage(@RequestHeader("Authorization") String token,
                                         @RequestParam("image") MultipartFile imageFile) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            byte[] imageBytes = imageFile.getBytes();  // Chuyển thành byte[] nếu hàm updateImage yêu cầu
            User updatedUser = userService.updateImage(email, imageBytes);

            return ResponseEntity.ok(Map.of(
                    "state", "success",
                    "msg", "✅ Update succeeded",
                    "data", updatedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "msg", e.getMessage()
            ));
        }
    }


}

