package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.OrderStatus;
import kiradev.studio.Eommerce.Enum.PaymentMethod;
import kiradev.studio.Eommerce.Enum.PaymentStatus;
import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.Payment;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.OrderService;
import kiradev.studio.Eommerce.service.PaymentService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class paymentController {

    private final JwtUtil jwtUtil;
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final UserService userService;

    public paymentController(JwtUtil jwtUtil, PaymentService paymentService, OrderService orderService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.paymentService = paymentService;
        this.orderService = orderService;
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
     * Validates the JWT token and checks if the user has admin permissions.
     *
     * @param token The JWT token from the request header.
     * @return A ResponseEntity containing the user's email or an error message.
     */
    @PostMapping("/createPayment")
    public ResponseEntity<?> createPayment(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID orderId,
            @RequestParam PaymentMethod paymentMethod
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            Order order = orderService.getOrderById(orderId);
            paymentService.createPayment(order, PaymentStatus.WAITING, paymentMethod);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Payment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }
    /**
     * Retrieves a payment by its ID.
     *
     * @param token The JWT token from the request header.
     * @param paymentId The ID of the payment to retrieve.
     * @return ResponseEntity containing the payment data or an error message.
     */
    @GetMapping("/getPaymentById/{paymentId}")
    public ResponseEntity<?> getPaymentById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID paymentId
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", paymentService.getPaymentById(paymentId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves the payment details for a specific order.
     *
     * @param token The JWT token from the request header.
     * @param orderId The ID of the order for which to retrieve payment details.
     * @return ResponseEntity containing the payment details or an error message.
     */
    @GetMapping("/getPaymentByOrderId")
    public ResponseEntity<?> getPaymentByOrderId(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID orderId
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(Map.of("state", "success", "data", paymentService.getPaymentByOrder(order)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all payments with pagination.
     *
     * @param token The JWT token from the request header.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return ResponseEntity containing the paginated payment data or an error message.
     */
    @GetMapping("/allPaged")
    public ResponseEntity<?> getAllPaymentsPaged(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("created_at").descending());
            Page<Payment> paymentPage = paymentService.getAllPayments(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("state", "success");
            response.put("data", paymentPage.getContent());
            response.put("currentPage", paymentPage.getNumber());
            response.put("totalItems", paymentPage.getTotalElements());
            response.put("totalPages", paymentPage.getTotalPages());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }


    /**
     * Updates the payment status for a specific order.
     *
     * @param token The JWT token from the request header.
     * @param orderId The ID of the order for which to update the payment status.
     * @param paymentStatus The new payment status to set.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PutMapping("/updatePaymentStatus")
    public ResponseEntity<?> updatePaymentStatus(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID orderId,
            @RequestParam PaymentStatus paymentStatus
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            Order order = orderService.getOrderById(orderId);
            paymentService.updatePaymentStatus(order, paymentStatus);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Payment status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Updates the payment method for a specific order.
     *
     * @param token The JWT token from the request header.
     * @param orderId The ID of the order for which to update the payment method.
     * @param paymentMethod The new payment method to set.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PutMapping("/updatePaymentMethod")
    public ResponseEntity<?> updatePaymentMethod(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID orderId,
            @RequestParam PaymentMethod paymentMethod
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            Order order = orderService.getOrderById(orderId);
            paymentService.updatePaymentMethod(order, paymentMethod);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Payment method updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Deletes a payment for a specific order.
     *
     * @param token The JWT token from the request header.
     * @param orderId The ID of the order for which to delete the payment.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @DeleteMapping("/deletePayment")
    public ResponseEntity<?> deletePayment(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID orderId
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        try {
            Order order = orderService.getOrderById(orderId);
            paymentService.deletePayment(order);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Payment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Retrieves all payments for admin users.
     *
     * @param token The JWT token for authentication.
     * @return A ResponseEntity containing the list of all payments or an error message.
     */
    @GetMapping("/getAllPayments")
    public ResponseEntity<?> getAllPayments(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User userAdmin = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!userService.hasPermission(userAdmin, UserRole.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("state", "fail", "msg", "❌ You do not have permission to access this resource"));
        }

        try {
            return ResponseEntity.ok(Map.of("state", "success", "data", paymentService.getAllPayment()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }
}
