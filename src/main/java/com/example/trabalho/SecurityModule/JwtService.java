package com.example.trabalho.SecurityModule;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:86400000}") long expiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );

        this.expiration = expiration;
    }

    // Gerar token JWT
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();
        Date expirationDate = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    // Extrair o usuário identificado pelo token
    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    // Validar token
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

        try {
            String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && extractClaims(token).getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extrair informações do token
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}