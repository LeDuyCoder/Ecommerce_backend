package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.dto.BankDTO;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.BankRepository;
import kiradev.studio.Eommerce.service.BankService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bank")
public class bankController {
    private final BankService bankService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public bankController(BankService bankService, UserService userService, JwtUtil jwtUtil) {
        this.bankService = bankService;
        this.userService = userService;
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

    //write api add bank
    @PutMapping("/addBank")
    public ResponseEntity<?> addBank(@RequestHeader("Authorization") String token,
                                     @RequestBody BankDTO bankDTO) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        System.out.println("add bank account: " + bankDTO.getOwner() + " " + bankDTO.getNumberCard() + " " + bankDTO.getBankName() + " " + bankDTO.getAddress() + " " + bankDTO.getCvv() + " " + bankDTO.getZipcode());

        if(bankDTO.getNumberCard() != null && bankDTO.getNumberCard().length() > 16){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ Invalid card number"));
        }


        try {
            if(bankService.isExistBank(bankDTO.getOwner(), bankDTO.getNumberCard())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ Bank account already exists"));
            }else {
                bankService.createBankAccount(bankDTO, user.getID());
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("msg", "✅ Bank account created successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", e.getMessage()));
        }
    }

    //write api get banks
    @GetMapping("/getBank")
    public ResponseEntity<?> getBank(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        try {
            return ResponseEntity.ok(Map.of("data", bankService.getBankUserID(user.getID())));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", e.getMessage()));
        }
    }

    //write api delete bank
    @DeleteMapping("/deleteBank")
    public ResponseEntity<?> deleteBank(@RequestHeader("Authorization") String token,
                        @RequestParam String owner,
                        @RequestParam String numberCard) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        try {
            bankService.deleteBankAccount(owner, numberCard);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "✅ Bank account deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", e.getMessage()));
        }
    }
}
