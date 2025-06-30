package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.MethodApply;
import kiradev.studio.Eommerce.Enum.MethodReduce;
import kiradev.studio.Eommerce.dto.VoucherDTO;
import kiradev.studio.Eommerce.dto.VoucherUpdateDTO;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.service.VoucherService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher")
public class voucherController {

    private final JwtUtil jwtUtil;
    private final VoucherService voucherService;
    private final UserService userService;


    public voucherController(JwtUtil jwtUtil, VoucherService voucherService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.voucherService = voucherService;
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
     * Creates a voucher for the authenticated user.
     *
     * @param token      The JWT token from the request header.
     * @param voucherDTO The voucher details to be created.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PostMapping("createVoucher")
    public ResponseEntity<?> createVoucher(
            @RequestHeader("Authorization") String token,
            @RequestBody VoucherDTO voucherDTO) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        String email = (String) tokenValidation.getBody();
        User user = userService.findByEmail(email).get();

        try{
            voucherService.CreateVoucher(user, voucherDTO.getCode(), voucherDTO.getDescription(), voucherDTO.getDiscountAmount(), voucherDTO.getExpirationDate(), voucherDTO.getMinimumOrderValue(), voucherDTO.getMethodReduce(), voucherDTO.getMethodApply());
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Voucher created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error creating voucher: " + e.getMessage()));
        }
    }

    /**
     * Updates the discount amount of a voucher.
     *
     * @param token          The JWT token from the request header.
     * @param code           The code of the voucher to be updated.
     * @param discountAmount The new discount amount for the voucher.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PutMapping("/updateDiscountAmountVoucher/{code}")
    public ResponseEntity<?> updateDiscountAmountVoucher(
            @RequestHeader("Authorization") String token,
            @PathVariable String code,
            @RequestParam double discountAmount) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            voucherService.UpdateDiscountAmountVoucher(code, discountAmount);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Discount amount updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Voucher not found or error updating discount amount"));
        }
    }

    /**
     * Updates a voucher with the provided details.
     *
     * @param token            The JWT token from the request header.
     * @param code             The code of the voucher to be updated.
     * @param voucherUpdateDTO The details to update the voucher with.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @PutMapping("/updateVoucher/{code}")
    public ResponseEntity<?> updateVoucher(
            @RequestHeader("Authorization") String token,
            @PathVariable String code,
            @RequestBody VoucherUpdateDTO voucherUpdateDTO) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {

            voucherService.updateVoucher(code, voucherUpdateDTO);

            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Voucher updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Voucher not found or error updating voucher"));
        }
    }


    /**
     * Retrieves a voucher by its code.
     *
     * @param token The JWT token from the request header.
     * @param code  The code of the voucher to retrieve.
     * @return ResponseEntity containing the voucher details or an error message.
     */
    @GetMapping("/getVoucherByCode/{code}")
    public ResponseEntity<?> getVoucherByCode(
            @RequestHeader("Authorization") String token,
            @PathVariable String code) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            return ResponseEntity.ok(voucherService.getVoucherByCode(code));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Voucher not found"));
        }
    }

    /**
     * Retrieves all vouchers.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity containing the list of vouchers or an error message.
     */
    @GetMapping("/getAllVouchers")
    public ResponseEntity<?> getAllVouchers(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            return ResponseEntity.ok(voucherService.getAllVouchers());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error retrieving vouchers"));
        }
    }

    /**
     * Retrieves all vouchers that have expired.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity containing the list of expired vouchers or an error message.
     */
    @GetMapping("/getVouchersExpiredDate")
    public ResponseEntity<?> getVouchersExpiredDate(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            return ResponseEntity.ok(voucherService.getVouchersExpiredDate());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error retrieving expired vouchers"));
        }
    }

    /**
     * Retrieves all vouchers that have not expired.
     *
     * @param token The JWT token from the request header.
     * @return ResponseEntity containing the list of non-expired vouchers or an error message.
     */
    @GetMapping("/getVouchersNotExpiredDate")
    public ResponseEntity<?> getVouchersNotExpiredDate(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            return ResponseEntity.ok(voucherService.getVouchersNotExpiredDate());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("state", "fail", "msg", "❌ Error retrieving non-expired vouchers"));
        }
    }

    /**
     * Deletes a voucher by its code.
     *
     * @param token The JWT token from the request header.
     * @param code  The code of the voucher to delete.
     * @return ResponseEntity indicating success or failure of the operation.
     */
    @DeleteMapping("/deleteVoucher/{code}")
    public ResponseEntity<?> deleteVoucher(
            @RequestHeader("Authorization") String token,
            @PathVariable String code) {
        ResponseEntity<?> tokenValidation = validateToken(token);
        if (tokenValidation.getStatusCode() != HttpStatus.OK) {
            return tokenValidation;
        }

        try {
            voucherService.deleteVoucher(code);
            return ResponseEntity.ok(Map.of("state", "success", "msg", "✅ Voucher deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("state", "fail", "msg", "❌ Voucher not found"));
        }
    }

}
