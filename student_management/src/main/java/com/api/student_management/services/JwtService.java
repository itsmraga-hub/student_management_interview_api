package com.api.student_management.services;

import com.api.student_management.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
//@Slf4j
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtService.class);
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }

    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User userDetails) {
        Map<String, Object> extraClaims = new HashMap<>(Map.of("email", userDetails.getEmail()));
        extraClaims.put("id", userDetails.getId());
        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, User userDetails) {

        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, Optional<User> userDetails) {

        final String username = extractUsername(token);
//        logger.info("userDetails: {}", userDetails.get().getUsername());
//        logger.info("username: {}", username);
//        logger.info(" username.equals(userDetails.get().getUsername()): {}",  username.equals(userDetails.get().getUsername()));
//        logger.info("isTokenExpired: {}", isTokenExpired(token));
//        logger.info("userDetails: {}", userDetails);
//        logger.info("username.equals(userDetails.getUsername()): {}", username.equals(userDetails.get().getEmail()));
//        logger.info("isTokenValid111: {}", username.equals(userDetails.get().getEmail()) &&!isTokenExpired(token));
        return (username.equals(userDetails.get().getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
//        logger.info("isTokenExpired: {}", extractExpiration(token).before(new Date()));
//        logger.info("token: {}", token);
//        logger.info("extractExpiration: {}", extractExpiration(token));
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}