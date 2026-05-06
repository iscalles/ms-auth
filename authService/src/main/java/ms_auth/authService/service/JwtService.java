package ms_auth.authService.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret:misecretatuysuperaguantadadesdeprotegidodelmundoentero}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generarAccessToken(String rutUsuario, String tipoRol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tipoRol", tipoRol);
        return crearToken(claims, rutUsuario, expiration);
    }

    public String generarRefreshToken(String rutUsuario) {
        Map<String, Object> claims = new HashMap<>();
        return crearToken(claims, rutUsuario, refreshExpiration);
    }

    private String crearToken(Map<String, Object> claims, String subject, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerRut(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public String extraerRol(String token) {
        Claims claims = extraerTodasLasClaims(token);
        return (String) claims.get("tipoRol");
    }

    private <T> T extraerClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodasLasClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodasLasClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long obtenerTiempoExpiracion() {
        return expiration / 1000;
    }
}