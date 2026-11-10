package com.gdg.library.security;

import com.gdg.library.user.Role;
import com.gdg.library.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final Clock clock;
    private final long expirationSeconds;
    private final JwtParser parser;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration-seconds}") long expirationSeconds, Clock clock) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        if (expirationSeconds <= 0) throw new IllegalArgumentException("JWT expiration must be positive");
        this.clock = clock;
        this.expirationSeconds = expirationSeconds;
        this.parser = Jwts.parser().verifyWith(key)
                .clock(() -> Date.from(clock.instant())).build();
    }

    public String issue(User user) {
        Instant now = clock.instant();
        return Jwts.builder().subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    public LoginUser parse(String token) {
        // 마지막 문자의 미사용 비트만 바꾸는 변조도 거절하도록 정규 Base64URL인지 확인한다.
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3 || !Base64.getUrlEncoder().withoutPadding()
                .encodeToString(Base64.getUrlDecoder().decode(parts[2])).equals(parts[2])) {
            throw new IllegalArgumentException("Invalid signature encoding");
        }
        Claims claims = parser.parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null) throw new IllegalArgumentException("Missing expiration");
        String subject = claims.getSubject();
        String role = claims.get("role", String.class);
        if (subject == null || role == null) throw new IllegalArgumentException("Missing identity");
        Long id = Long.valueOf(subject);
        if (id <= 0) throw new IllegalArgumentException("Invalid subject");
        return new LoginUser(id, Role.valueOf(role));
    }
}
