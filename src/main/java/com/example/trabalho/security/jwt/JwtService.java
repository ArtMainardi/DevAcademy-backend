package com.example.trabalho.security.jwt;

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

    public JwtService(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtProperties.getSecret())
        );

        this.expiration = jwtProperties.getExpiration();
    }

    /**
     * Gera um token JWT para o usuário autenticado.
     */
    public String generateToken(UserDetails userDetails) {

        Date issuedAt = new Date();
        Date expirationDate = new Date(
                issuedAt.getTime() + expiration
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrai o identificador do usuário (email).
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extrai a data de expiração do token.
     */
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    /**
     * Retorna o tempo de validade configurado em milissegundos.
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * Verifica se o token corresponde ao usuário autenticado
     * e se ainda está dentro do prazo de validade.
     */
    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {
        try {
            Claims claims = extractClaims(token);

            return claims.getSubject().equals(
                    userDetails.getUsername()
            )
                    && claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extrai e valida as informações do token.
     *
     * A biblioteca verifica automaticamente a assinatura
     * e a expiração durante o processamento.
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}