package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.service.VisitorService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/Visitor")
public class visitorController {

    private final JwtUtil jwtUtil;
    private final VisitorService visitorService;
    private final UserService userService;

    @Autowired
    public visitorController(JwtUtil jwtUtil, VisitorService visitorService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.visitorService = visitorService;
        this.userService = userService;
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
     * Creates a visitor record for the authenticated user.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PostMapping("/createVisitor")
    public ResponseEntity<?> createVisitor(@RequestHeader("Authorization")  String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        String email = (String) validation.getBody();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            visitorService.AddVistor(user, LocalDate.now());
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Visitor created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves the total number of visitors.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity containing the total count of visitors or an error message.
     */
    @GetMapping("getAllVisitorsCount")
    public ResponseEntity<?> getAllVisitorsCount(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            long count = visitorService.getTotalVisitors();
            return ResponseEntity.ok(Map.of("state", "success", "count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all visitors for a specific date.
     *
     * @param token The JWT token from the request header.
     * @param date  The date for which to retrieve visitors.
     * @return ResponseEntity containing the list of visitors or an error message.
     */
    @GetMapping("getAllVisitorsByDate")
    public ResponseEntity<?> getAllVisitorsByDate(@RequestHeader("Authorization") String token,
                                               @RequestParam LocalDate date) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", visitorService.getAllVistorsByDate(date)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all visitors for a specific month and year.
     *
     * @param token The JWT token from the request header.
     * @param year  The year for which to retrieve visitors.
     * @param month The month for which to retrieve visitors.
     * @return ResponseEntity containing the list of visitors or an error message.
     */
    @GetMapping("getAllVisitorsByMonth")
    public ResponseEntity<?> getAllVisitorsByMonth(@RequestHeader("Authorization") String token,
                                                   @RequestParam int year,
                                                   @RequestParam int month) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", visitorService.getVisitorsInMonth(year, month)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all visitors for a specific year.
     *
     * @param token The JWT token from the request header.
     * @param year  The year for which to retrieve visitors.
     * @return ResponseEntity containing the list of visitors or an error message.
     */
    @GetMapping("getAllVisitorsByYear")
    public ResponseEntity<?> getAllVisitorsByYear(@RequestHeader("Authorization") String token,
                                                  @RequestParam int year) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", visitorService.getVisitorsByYear(year)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }
}
