package org.example.ruwaa.Config.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

import static java.util.logging.Level.parse;

@Component
public class JwtUtil
{

    @Value("${jwt.secret}")
    private String secret;
    private final long expiration = 60 * 60 * 1000;

    public String generateToken(UserDetails user){
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return parse(token).getBody().getSubject();
    }

    public boolean isValid(String token){
        return !parse(token).getBody().getExpiration().before(new Date());
    }

    private Jws<Claims> parse(String token){
        return Jwts.parser().setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token);
    }


}
