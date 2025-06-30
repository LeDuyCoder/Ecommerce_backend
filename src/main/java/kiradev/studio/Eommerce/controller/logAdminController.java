package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.service.LogAdminService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/logAdmin")
public class logAdminController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final LogAdminService logAdminService;

    @Autowired
    public logAdminController(JwtUtil jwtUtil, UserService userService, LogAdminService logAdminService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.logAdminService = logAdminService;
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

    /**
     * Validates the JWT token and checks if the user has admin permissions.
     *
     * @param token The JWT token from the request header.
     * @return A ResponseEntity containing the user's email or an error message.
     */
    @PostMapping("/addLog")
    public ResponseEntity<?> addLog(@RequestHeader("Authorization") String token,
                                    @RequestParam("action") String action) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        String email = (String) validation.getBody();
        var userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to add logs"));
        }

        logAdminService.addLog(userAdmin, action);
        return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Log added successfully"));
    }

    /**
     * Retrieves all logs for admin users.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the logs or an error message.
     */
    @GetMapping("/getAllLogs")
    public ResponseEntity<?> getAllLogs(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        String email = (String) validation.getBody();
        var userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println(userAdmin.getRole());

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to view logs"));
        }

        return ResponseEntity.ok(Map.of("state", "success", "logs", logAdminService.getAllLogs()));
    }

    /**
     * Retrieves logs for a specific user by admin.
     *
     * @param token       The JWT token for authentication.
     * @param emailAdmin  The email of the user whose logs are to be retrieved.
     * @return A ResponseEntity containing the user's logs or an error message.
     */
    @GetMapping("/getLogsByUserAdmin")
    public ResponseEntity<?> getLogsByUserAdmin(@RequestHeader("Authorization") String token,
                                                @RequestParam("email") String emailAdmin) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        String email = (String) validation.getBody();
        var userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to view logs"));
        }


        var user = userService.findByEmail(emailAdmin).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(Map.of("state", "success", "logs", logAdminService.getLogsByUserAdmin(user)));
    }
}
