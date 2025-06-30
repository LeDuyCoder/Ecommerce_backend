package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderVoucher;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.entity.Voucher;
import kiradev.studio.Eommerce.service.OrderService;
import kiradev.studio.Eommerce.service.OrderVoucherService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.service.VoucherService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ordervoucher")
public class orderVoucherController {

    private final JwtUtil jwtUtil;
    private final OrderVoucherService orderVoucherService;
    private final VoucherService voucherService;
    private final UserService userService;
    private final OrderService orderService;

    public orderVoucherController(JwtUtil jwtUtil, OrderVoucherService orderVoucherService, VoucherService voucherService, UserService userService, OrderService orderService) {
        this.jwtUtil = jwtUtil;
        this.orderVoucherService = orderVoucherService;
        this.voucherService = voucherService;
        this.userService = userService;
        this.orderService = orderService;
    }

    /**
     * Validates the JWT token from the request header.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity containing the email if valid, or an error message if invalid.
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
     * Applies a voucher to an order.
     *
     * @param token       The JWT token from the request header.
     * @param voucherCode The code of the voucher to be applied.
     * @param orderId     The ID of the order to which the voucher is applied.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PostMapping("/applyVoucher")
    public ResponseEntity<?> applyVoucherToOrder(@RequestHeader("Authorization") String token,
                                                 @RequestParam String voucherCode,
                                                 @RequestParam UUID orderId
    ) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Voucher voucher = voucherService.getVoucherByCode(voucherCode);
            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order not found"));
            }

            if (voucher == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Voucher not found"));
            }

            if (voucherService.validateVoucherExpiryDate(voucher)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("state", "fail", "msg", "❌ Voucher has expired"));
            }

            if (orderVoucherService.isVoucherApplied(voucher, user)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("state", "fail", "msg", "❌ Voucher already applied to this order"));
            }

            orderVoucherService.applyVoucherToOrder(voucher, order);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Voucher applied successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Removes a voucher from an order.
     *
     * @param token   The JWT token from the request header.
     * @param orderId The ID of the order from which the voucher is removed.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @GetMapping("getVoucherByOrder")
    public ResponseEntity<?> getVoucherByOrder(@RequestHeader("Authorization") String token,
                                               @RequestParam UUID orderId) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order not found"));
            }

            List<OrderVoucher> vouchers = orderVoucherService.getOrderVouchersByOrder(order);
            List<Voucher> voucherList = vouchers.stream()
                    .map(OrderVoucher::getVoucher)
                    .toList();
            if (voucherList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ No voucher applied to this order"));
            }

            return ResponseEntity.ok(Map.of("state", "success", "data", voucherList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    /**
     * Checks if a voucher is already applied to an order by a specific user.
     *
     * @param token       The JWT token from the request header.
     * @param voucherCode The code of the voucher to check.
     * @return ResponseEntity indicating whether the voucher is applied or not.
     */
    @GetMapping("/checkVoucherApplied")
    public ResponseEntity<?> checkVoucherApplied(@RequestHeader("Authorization") String token,
                                                 @RequestParam String voucherCode) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            Voucher voucher = voucherService.getVoucherByCode(voucherCode);
            if (voucher == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Voucher not found"));
            }

            boolean isApplied = orderVoucherService.isVoucherApplied(voucher, user);
            return ResponseEntity.ok(Map.of("state", "success", "isApplied", isApplied));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }
}
