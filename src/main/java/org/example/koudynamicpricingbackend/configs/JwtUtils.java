package org.example.koudynamicpricingbackend.configs;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtils {

    private final SecretKey signingKey;
    private final long accessTokenValidityMs;
    private final long refreshTokenValidityMs;

    public JwtUtils(

            @Value("${jwt.secret}")
            String base64Secret,
            @Value("${jwt.access-token-validity-ms:900000}")
            long accessTokenValidityMs,   // default 15 min
            @Value("${jwt.refresh-token-validity-ms:604800000}")
            long refreshTokenValidityMs // default 7 days

    ) {
        // base64Secret should be sufficiently long (e.g. 256+ bit)
        this.signingKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.accessTokenValidityMs = accessTokenValidityMs;
        this.refreshTokenValidityMs = refreshTokenValidityMs;


    }

    public String generateAccessToken(String email, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessTokenValidityMs);
        String jti = UUID.randomUUID().toString(); // unique id for blacklisting

        return Jwts.builder()
                .setSubject(email)
                .setId(jti)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshTokenValidityMs);
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(email)
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- Validation & claim access ---

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        return getAllClaims(token).getSubject();
    }

//    public Long getUserIdFromToken(String token) {
//        Object v = getAllClaims(token).get("userId");
//        if (v instanceof Integer) return ((Integer) v).longValue();
//        if (v instanceof Long) return (Long) v;
//        if (v instanceof String) return Long.parseLong((String) v);
//        return null;
//    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Object roles = getAllClaims(token).get("roles");
        if (roles == null) return Collections.emptyList();
        return (List<String>) roles;
    }

    public String getJtiFromToken(String token) {
        return getAllClaims(token).getId();
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaims(token).getExpiration();
    }

    public long getRemainingValiditySeconds(String token) {
        Date exp = getExpirationDateFromToken(token);
        long remainingMs = exp.getTime() - System.currentTimeMillis();
        return remainingMs > 0 ? remainingMs / 1000L : 0L;
    }
}
