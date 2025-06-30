package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.OrderService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/Order")
public class orderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public orderController(OrderService orderService, JwtUtil jwtUtil, UserService userService) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
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
     * Creates a new order for the authenticated user.
     *
     * @param token      The JWT token for authentication.
     * @param totalPrice The total price of the order (optional).
     * @return A ResponseEntity containing the result of the operation.
     */
    @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestHeader("Authorization") String token,
                                         @RequestParam(required = false) Float totalPrice
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            orderService.createOrder(user, totalPrice == null ? 0 : totalPrice);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Order created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param token   The JWT token for authentication.
     * @param orderId The ID of the order to retrieve.
     * @return A ResponseEntity containing the order data or an error message.
     */
    @GetMapping("/getOrderById")
    public ResponseEntity<?> getOrderById(@RequestHeader("Authorization") String token,
                                          @RequestParam UUID orderId) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", orderService.getOrderById(orderId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    //write documentation for deleteOrder method
    /**
     * Deletes an order by its ID.
     *
     * @param token   The JWT token for authentication.
     * @param orderId The ID of the order to delete.
     * @return A ResponseEntity indicating success or failure of the deletion operation.
     */
    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestHeader("Authorization") String token,
                                         @PathVariable UUID orderId) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Order deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all orders for the authenticated user.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the list of orders or an error message.
     */
    @GetMapping("/getOrdersByUser")
    public ResponseEntity<?> getOrdersByUser(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(Map.of("state", "success", "data", orderService.getAllOrdersByUser(user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all orders for admin users.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the list of all orders or an error message.
     */
    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllOrders(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User userAdmin = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }

        try {
            // Gợi ý: kiểm tra role admin nếu có phân quyền
            return ResponseEntity.ok(Map.of("state", "success", "data", orderService.getAllOrders()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

}
