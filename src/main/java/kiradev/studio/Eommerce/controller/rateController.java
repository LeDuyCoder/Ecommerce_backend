package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.dto.RateDTO;
import kiradev.studio.Eommerce.service.ProductService;
import kiradev.studio.Eommerce.service.RateService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rate")
public class rateController {

    private final RateService rateService;
    private final UserService userService;
    private final ProductService productService;
    private final JwtUtil jwtUtil;


    public rateController(RateService rateService, UserService userService, ProductService productService, JwtUtil jwtUtil) {
        this.rateService = rateService;
        this.userService = userService;
        this.productService = productService;
        this.jwtUtil = jwtUtil;
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


    //write api get all rate by product id
    @GetMapping("/getAllRateByProductId")
    public ResponseEntity<?> getAllRateByProductId(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID productId) {
        ResponseEntity<?> validation = validateToken(token); // No token needed for this operation
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        if (productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ Product ID is required"));
        }

        try {
            return ResponseEntity.ok(Map.of("state", "success", "rates", rateService.getAllRatesByProductId(productId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error fetching rates: " + e.getMessage()));
        }
    }

    //write api get rate by user id and product id
    @GetMapping("/getRateByUserIdAndProductId")
    public ResponseEntity<?> getRateByUserIdAndProductId(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID userId,
            @RequestParam UUID productId) {
        ResponseEntity<?> validation = validateToken(token); // No token needed for this operation
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        if (userId == null || productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ User ID and Product ID are required"));
        }

        try {
            return ResponseEntity.ok(Map.of("state", "success", "rate", rateService.getRateByUserIdAndProductId(userId, productId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error fetching rate: " + e.getMessage()));
        }
    }

    //write api create rate
    @PostMapping("/createRate")
    public ResponseEntity<?> createRate(
            @RequestHeader("Authorization") String token,
            @RequestBody RateDTO rateDTO
            ) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        if (rateDTO.getUserId() == null || rateDTO.getProductId() == null || rateDTO.getRate() < 1 || rateDTO.getRate() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ Invalid input parameters"));
        }

        try {
            rateService.addRate(rateDTO.getUserId(), rateDTO.getProductId(), rateDTO.getRate(), rateDTO.getComment());
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Rate created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error creating rate: " + e.getMessage()));
        }
    }

    //write api update rate
    @PutMapping("/updateRate")
    public ResponseEntity<?> updateRate(
            @RequestHeader("Authorization") String token,
            @RequestBody RateDTO rateDTO) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        if (rateDTO.getUserId() == null || rateDTO.getProductId() == null || rateDTO.getRate() < 1 || rateDTO.getRate() > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ Invalid input parameters"));
        }

        try {
            rateService.updateRate(rateDTO.getUserId(), rateDTO.getProductId(), rateDTO.getRate(), rateDTO.getComment());
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Rate updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error updating rate: " + e.getMessage()));
        }
    }

    //write api delete rate
    @DeleteMapping("/deleteRate")
    public ResponseEntity<?> deleteRate(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID userId,
            @RequestParam UUID productId) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        if (userId == null || productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("state", "fail", "msg", "❌ User ID and Product ID are required"));
        }
        try {
            rateService.deleteRate(userId, productId);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Rate deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error deleting rate: " + e.getMessage()));
        }
    }

}
