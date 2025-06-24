package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.dto.OrderItemDTO;
import kiradev.studio.Eommerce.entity.Order;
import kiradev.studio.Eommerce.entity.OrderItem;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.service.OrderItemService;
import kiradev.studio.Eommerce.service.OrderService;
import kiradev.studio.Eommerce.service.ProductService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orderItems")
public class orderItemController {

    private final OrderItemService orderItemService;
    private final OrderService orderService;
    private final ProductService productService;
    private final JwtUtil jwtUtil;

    public orderItemController(OrderItemService orderItemService, OrderService orderService, ProductService productService, JwtUtil jwtUtil) {
        this.orderItemService = orderItemService;
        this.orderService = orderService;
        this.productService = productService;
        this.jwtUtil = jwtUtil;
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

    @PutMapping("/addOrderItem")
    public ResponseEntity<?> addOrderItem(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderItemDTO orderItemDTO) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Order order = orderService.getOrderById(orderItemDTO.getOrderID());
            Products products = productService.getProductById(orderItemDTO.getProductID());
            if (order == null || products == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order or Product not found"));
            }
            OrderItem orderItem = orderItemService.getOrderItemByOrderAndProduct(order, products);
            if( orderItem != null) {
                // If the item already exists, update the quantity
                orderItemService.updateItemQuantityInOrder(order, products, orderItemDTO.getQuantity());
            } else {
                // If the item does not exist, add it
                orderItemService.addItemToOrder(order, products, orderItemDTO.getQuantity(), products.getPrice());
            }
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Order item added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    @PutMapping("/addListOrderItem")
    public ResponseEntity<?> addListOrderItem(
            @RequestHeader("Authorization") String token,
            @RequestBody List<OrderItemDTO> orderItemDTOs) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        Map<String, String> result = new LinkedHashMap<>();
        int successCount = 0;

        try {
            Map<UUID, Order> orderCache = new HashMap<>();
            Map<UUID, Products> productCache = new HashMap<>();

            for (OrderItemDTO dto : orderItemDTOs) {
                UUID orderId = dto.getOrderID();
                UUID productId = dto.getProductID();

                Order order = orderCache.computeIfAbsent(orderId, orderService::getOrderById);
                Products product = productCache.computeIfAbsent(productId, productService::getProductById);

                if (order == null || product == null) {
                    result.put(dto.toString(), "❌ Order or Product not found");
                    continue;
                }

                try {
                    orderItemService.addItemToOrder(order, product, dto.getQuantity(), product.getPrice());
                    successCount++;
                } catch (Exception ex) {
                    result.put(dto.toString(), "❌ Failed to add: " + ex.getMessage());
                }
            }

            if (successCount == orderItemDTOs.size()) {
                return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ All items added"));
            } else {
                return ResponseEntity.status(HttpStatus.MULTI_STATUS) // 207
                        .body(Map.of(
                                "state", "partial",
                                "successCount", successCount,
                                "failCount", orderItemDTOs.size() - successCount,
                                "details", result
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }


    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderItemsByOrderId(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID orderId) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Order order = orderService.getOrderById(orderId);
            List<OrderItem> items = orderItemService.getOrderItemsByOrderId(order);
            return ResponseEntity.ok(Map.of("state", "success", "data", items));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    @GetMapping("/getOrderItemById/{orderItemId}")
    public ResponseEntity<?> getOrderItemById(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID orderItemId) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            OrderItem orderItem = orderItemService.getOrderItemById(orderItemId);
            if (orderItem == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order item not found"));
            }
            return ResponseEntity.ok(Map.of("state", "success", "data", orderItem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    @DeleteMapping("/removeOrderItem/{id}")
    public ResponseEntity<?> deleteOrderItemAll(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID id) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Order order = orderService.getOrderById(id);

            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order not found"));
            }
            orderItemService.clearOrderItems(order);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    @DeleteMapping("/removeItemInOrderItem")
    public ResponseEntity<?> removeItemInOrderItem(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderItemDTO orderItemDTO) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Order order = orderService.getOrderById(orderItemDTO.getOrderID());
            Products products = productService.getProductById(orderItemDTO.getProductID());
            if (order == null || products == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order or Product not found"));
            }
            orderItemService.removeItemFromOrder(order, products);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Order item removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

    @PutMapping("/updateQuantity")
    public ResponseEntity<?> updateOrderItemQuantity(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderItemDTO dto) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        try {
            Order order = orderService.getOrderById(dto.getOrderID());
            Products product = productService.getProductById(dto.getProductID());
            if (order == null || product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("state", "fail", "msg", "❌ Order or Product not found"));
            }

            orderItemService.updateItemQuantityInOrder(order, product, dto.getQuantity());
            return ResponseEntity.ok(Map.of("state", "success", "msg", "Quantity updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ " + e.getMessage()));
        }
    }

}
