package kiradev.studio.Eommerce.controller;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.entity.Shop;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.service.ShopService;
import kiradev.studio.Eommerce.service.UserService;
import kiradev.studio.Eommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/shop")
public class shopController {

    private final ShopService shopService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public shopController(ShopService shopService, UserService userService, JwtUtil jwtUtil) {
        this.shopService = shopService;
        this.userService = userService;
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

    /**
     * Retrieves a shop by its ID.
     *
     * @param shopID the UUID of the shop to retrieve
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the shop is found.
     *                 <br>Body: JSON representation of the {@link Shop} object.</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *             <li><b>404 Not Found</b> – If no shop with the given ID exists.
     *                 <br>Body: {"msg": "❌ No shop found"}</li>
     *         </ul>
     */
    @GetMapping("/by-id/{shopID}")
    public ResponseEntity<?> getShopById(@PathVariable UUID shopID,
                                         @RequestHeader("Authorization") String token) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        Shop shop = shopService.getShopById(shopID);
        if (shop == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", "❌ No shop found"));
        }
        return ResponseEntity.ok(shop);
    }

    /**
     * Retrieves a shop by the owner's user ID.
     *
     * @param userID the UUID of the user who owns the shop
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the shop is found.
     *                 <br>Body: JSON representation of the {@link Shop} object.</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *             <li><b>404 Not Found</b> – If no shop owned by the user exists.
     *                 <br>Body: {"msg": "❌ No shop found"}</li>
     *         </ul>
     */
    @GetMapping("/by-user/{userID}")
    public ResponseEntity<?> getShopByUserId(@PathVariable UUID userID,
                                             @RequestHeader("Authorization") String token) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;

        Shop shop = shopService.getShopsByOwner(userID);
        if (shop == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", "❌ No shop found"));
        }
        return ResponseEntity.ok(shop);
    }

    /**
     * Retrieves all shops.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If shops are found.
     *                 <br>Body: List of {@link Shop} objects.</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *             <li><b>404 Not Found</b> – If no shops are found.
     *                 <br>Body: {"msg": "❌ No shops found"}</li>
     *         </ul>
     */
    @GetMapping("/all")
    public ResponseEntity<?>  getAllShops(@RequestHeader("Authorization") String token) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).get();


        System.out.println(user.getID());
        if(userService.hasPermission(user, UserRole.ADMIN)){
            List<Shop> shops = shopService.getAllShops();
            if (shops.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", "❌ No shops found"));
            }
            return ResponseEntity.ok(shops);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("msg", "❌ You don't have permission to access this resource"));
        }
    }

    /**
     * Creates a new shop.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param name the name of the shop
     * @param description the description of the shop
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>201 Created</b> – If the shop is created successfully.
     *                 <br>Body: {"msg": "✅ Shop created successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the user already has a shop.
     *                 <br>Body: {"msg": "❌ You already have a shop"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *         </ul>
     */
    @PostMapping("/create")
    public ResponseEntity<?> createShop( @RequestHeader("Authorization") String token,
                            @RequestParam String name,
                            @RequestParam String description) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).get();
        if(shopService.isShopExist(user.getID())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ You already have a shop"));
        }

        shopService.createShop(name, description, user.getID());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("msg", "✅ Shop created successfully"));
    }

    /**
     * Updates an existing shop.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param shopID the UUID of the shop to update
     * @param name the new name of the shop (optional)
     * @param description the new description of the shop (optional)
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the shop is updated successfully.
     *                 <br>Body: {"msg": "✅ Shop updated successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the user already has a shop.
     *                 <br>Body: {"msg": "❌ You already have a shop"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *         </ul>
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateShop(@RequestHeader("Authorization") String token,
                            @RequestParam UUID shopID,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) String description) {

        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).get();
        if(shopService.isShopExist(user.getID())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ You already have a shop"));
        }

        shopService.updateShop(shopID, name, description, null);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "✅ Shop updated successfully"));
    }

    /**
     * Updates the image of an existing shop.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param shopID the UUID of the shop to update
     * @param imageFile the new image file for the shop
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the shop image is updated successfully.
     *                 <br>Body: {"msg": "✅ Shop image updated successfully"}</li>
     *             <li><b>400 Bad Request</b> – If no image file is provided or if the user already has a shop.
     *                 <br>Body: {"msg": "❌ No image file provided"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *         </ul>
     */
    @PutMapping(value = "/update/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateShopImage(@RequestHeader("Authorization") String token,
                            @RequestParam UUID shopID,
                            @RequestParam("image") MultipartFile imageFile) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).get();
        if(shopService.isShopExist(user.getID())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ You already have a shop"));
        }
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ No image file provided"));
        }
        try {
            byte[] imageBytes = imageFile.getBytes();
            shopService.updateShop(shopID, null, null, imageBytes);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "✅ Shop image updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("msg", "❌ Error updating shop image: " + e.getMessage()));
        }
    }

    /**
     * Deletes a shop by its ID.
     *
     * @param token the JWT token from the Authorization header for authentication
     * @param shopID the UUID of the shop to delete
     * @return ResponseEntity:
     *         <ul>
     *             <li><b>200 OK</b> – If the shop is deleted successfully.
     *                 <br>Body: {"msg": "✅ Shop deleted successfully"}</li>
     *             <li><b>400 Bad Request</b> – If the user already has a shop.
     *                 <br>Body: {"msg": "❌ You already have a shop"}</li>
     *             <li><b>401 Unauthorized</b> – If the token is invalid.
     *                 <br>Body: {"msg": "Unauthorized"}</li>
     *         </ul>
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteShop(@RequestHeader("Authorization") String token,
                            @RequestParam UUID shopID) {
        ResponseEntity<?> validation = validateToken(token);
        if (!validation.getStatusCode().is2xxSuccessful()) return validation;
        String email = (String) validation.getBody();

        User user = userService.findByEmail(email).get();
        if(shopService.isShopExist(user.getID())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("msg", "❌ You already have a shop"));
        }

        shopService.deleteShop(shopID);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("msg", "✅ Shop deleted successfully"));
    }

}
