package cn.muzisheng.pear;

import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.exception.AuthorizationException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilTest {
    private static final String EMAIL = "alice@example.com";
    private static final String STRONG_SECRET = "01234567890123456789012345678901";

    @Test
    void generatedTokenCanBeParsedBackToEmail() {
        JwtUtil jwtUtil = new JwtUtil(config().build());

        String token = jwtUtil.generateTokenWithEmail(EMAIL);

        assertEquals(EMAIL, jwtUtil.getEmailFromToken(token));
    }

    @Test
    void refreshTokenReturnsNewTokenWithSameEmail() {
        JwtUtil jwtUtil = new JwtUtil(config().build());
        String token = jwtUtil.generateTokenWithEmail(EMAIL);

        String refreshed = jwtUtil.refreshToken(token);

        assertNotEquals(token, refreshed);
        assertEquals(EMAIL, jwtUtil.getEmailFromToken(refreshed));
    }

    @Test
    void customHeadIsRequiredAndUsed() {
        JwtUtil jwtUtil = new JwtUtil(config().head("Token ").build());

        String token = jwtUtil.generateTokenWithEmail(EMAIL);

        assertTrue(token.startsWith("Token "));
        assertEquals(EMAIL, jwtUtil.getEmailFromToken(token));
        assertThrows(AuthorizationException.class, () -> jwtUtil.getEmailFromToken(token.replaceFirst("Token ", "Bearer ")));
    }

    @Test
    void wrongPrefixIsRejected() {
        JwtUtil jwtUtil = new JwtUtil(config().build());
        String token = jwtUtil.generateTokenWithEmail(EMAIL);

        assertThrows(AuthorizationException.class, () -> jwtUtil.getEmailFromToken(token.replaceFirst("Bearer ", "Token ")));
    }

    @Test
    void issuerMismatchIsRejected() {
        String token = new JwtUtil(config().issue("issuer-a").build()).generateTokenWithEmail(EMAIL);
        JwtUtil parser = new JwtUtil(config().issue("issuer-b").build());

        assertThrows(AuthorizationException.class, () -> parser.getEmailFromToken(token));
    }

    @Test
    void subjectMismatchIsRejected() {
        String token = new JwtUtil(config().subject("subject-a").build()).generateTokenWithEmail(EMAIL);
        JwtUtil parser = new JwtUtil(config().subject("subject-b").build());

        assertThrows(AuthorizationException.class, () -> parser.getEmailFromToken(token));
    }

    @Test
    void weakSecretIsRejected() {
        assertThrows(JwtException.class, () -> new JwtUtil(TokenConfig.builder().secret("short-secret").build()));
    }

    private static TokenConfig.Builder config() {
        return TokenConfig.builder()
                .secret(STRONG_SECRET)
                .expire(60_000L)
                .issue("pear-test")
                .subject("authentication-test");
    }
}
