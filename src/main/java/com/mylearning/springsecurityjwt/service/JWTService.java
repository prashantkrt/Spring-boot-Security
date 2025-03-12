package com.mylearning.springsecurityjwt.service;

import com.mylearning.springsecurityjwt.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    // using plaintext
    private final String rawSecretKey = "my-super-secret-key-12345";
    // base64 encoding my-super-secure-and-long-secret-key-1234567890
    private final String secretKey = "bXktc3VwZXItc2VjcmUtbG9uZy1zZWNyZXQta2V5LTEyMzQ1Njc4OTA=";

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        //claim Type and Claim Value
        //claims.put("Issuer", "Issuer");
        //claims.put("username", user.getUsername());
        //claims.put("role", user.getRole());
        //claims.put("Issued At", String.valueOf(System.currentTimeMillis()));
        //claims.put("Expiration", String.valueOf(System.currentTimeMillis() + 1000 * 60 * 60 * 24));
        // System.currentTimeMillis() gives the current time in milliseconds since January 1, 1970 (Unix Epoch).
        // System.currentTimeMillis() = 1741399200000
        // System.currentTimeMillis() → Current time in milliseconds.
        // 1000 * 60 * 60 * 24 → Converts 1 day to milliseconds:
        // 1000 ms = 1 second
        // 60 seconds = 1 minute
        // 60 minutes = 1 hour
        // 24 hours = 1 day
        // Total: 86,400,000 milliseconds (1 day).

        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(user.getUsername())
                .issuer("Issuer")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .and()
                .signWith(generateKey())
                .compact();
    }


    // Only the JWT Signature is hashed, NOT the payload!
    // JWT Structure (Before Signing)
    // Header  (Base64-encoded) + "." + Payload (Base64-encoded) + "." + Signature (HS256 hash)

    // How Verification Works
    // The server receives a JWT.
    // It extracts the header and payload (by Base64-decoding them).
    // It recomputes the signature using HS256:
    //  HMACSHA256(
    //      base64UrlEncode(header) + "." + base64UrlEncode(payload),
    //      secretKey
    //  )
    // It compares the recomputed signature with the one in the JWT:
    // If they match → Token is valid.
    // If they don’t match → Token is tampered with (invalid).

    // only secret key is hashed and won't be exposed but not the payload and header it can be decoded and viewed
    public SecretKey generateKey() {

        // Approach 1
        // Directly convert the plain secret string to a byte array
        // byte[] keyBytes = rawSecretKey.getBytes(StandardCharsets.UTF_8); // converting to byte array base64
        // return Keys.hmacShaKeyFor(keyBytes);

        // Approach 2 better
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey); // will be hashed using hs256
    }

    // will get claims and then fetch the username
    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
        // return extractClaim(token, claims -> claims.getSubject());
        // return extractClaim(token, new Function<Claims, String>() {
        //        @Override
        //        public String apply(Claims claims) {
        //            return claims.getSubject();
        //        }
        //    });
    }

    public <T> T extractClaims(String token, Function<Claims, T> chaimResolver) {
        Claims claims = extractAllClaims(token);
        return chaimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser() // Creates a JWT parser: Initializes a parser object that can read and verify tokens.
                .verifyWith(generateKey()) // Verifies the token's signature, Sets the key used to verify the token’s signature.
                .build() // Builds the JWT parser: After configuring the parser (like setting the verification key), you finalize it with .build().
                .parseSignedClaims(token) // Decodes the token, verifies the signature, and extracts the claims (payload data). If the token is invalid or expired, this will throw an exception.
                .getPayload(); // After parsing the token, you use .getPayload() to retrieve the claims object.
    }


    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
        // revision purpose
        // return extractClaim(token, claims -> claims.getExpiration());
        // return extractClaim(token, new Function<Claims, Date>() {
        //        @Override
        //        public Date apply(Claims claims) {
        //            return claims.getExpiration();
        //        }
        //    });

    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
