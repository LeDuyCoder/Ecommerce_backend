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
