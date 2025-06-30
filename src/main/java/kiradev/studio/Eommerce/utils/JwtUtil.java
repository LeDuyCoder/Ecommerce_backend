package kiradev.studio.Eommerce.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "B+^xLigrLuKwm=5yDmMahXqcZGVU7myjfC1HPirS-!2FvqGx8MSbH9q#qb!5GuiV"; // Phải >= 32 ký tự
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_TIME = 86400000; // 1 ngày

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }


    /**
     * Extracts the email from the JWT token.
     *
     * @param token the JWT token
     * @return the email extracted from the token
     */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Validates a JWT (JSON Web Token) by performing two key checks:
     *
     * 1. **Email Verification**: Extracts the email (usually stored as the "sub" or a custom claim)
     *    from the token and compares it with the provided `email` parameter to ensure that the token
     *    was originally issued for the intended user.
     *
     *    - The email is extracted by decoding the JWT's payload, typically using a JWT parsing library.
     *    - The token should contain a claim (e.g., "sub" or "email") which holds the user's email address.
     *    - This extracted value is then compared using `String.equals(...)` to the provided email.
     *
     * 2. **Expiration Check**: Verifies that the token has not expired.
     *    - This is usually done by checking the `exp` (expiration) claim in the token.
     *    - The token is considered invalid if the current time is after the expiration timestamp.
     *
     * Together, these checks confirm:
     * - The token was issued to the correct user (`email`)
     * - The token is still valid within its time-to-live window
     *
     * @param token the JWT token to validate
     * @param email the email address to compare against the email extracted from the token
     * @return true if the token is not expired and was issued for the given email; false otherwise
     */
    public boolean validateToken(String token, String email) {
        return email.equals(extractEmail(token)) && !isTokenExpired(token);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date of the token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks whether the given JWT token has expired.
     *
     * <p>This method extracts the "exp" (expiration) claim from the token's payload
     * and compares it with the current system time.</p>
     *
     * <ul>
     *   <li>If the expiration date is before the current time, the token is considered expired.</li>
     *   <li>If the expiration date is after or equal to the current time, the token is still valid.</li>
     * </ul>
     *
     * <p>Requires that the JWT contains a valid "exp" (expiration) claim in standard UNIX time format.</p>
     *
     * @param token the JWT token to be checked
     * @return {@code true} if the token is expired, {@code false} otherwise
     */
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
