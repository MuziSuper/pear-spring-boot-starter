package cn.muzisheng.pear.service;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@SpringBootTest
class JwtUtilTests {

    @Mock
    private TokenProperties tokenProperties;
    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(tokenProperties.getTokenExpire()).thenReturn(1000000000L);
        when(tokenProperties.getTokenHead()).thenReturn("Bearer ");
        when(tokenProperties.getTokenSalt()).thenReturn("secret");
    }

    @Test
    void generateTokenWithEmail_normal() {
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        assertNotNull(token);
    }

    @Test
    void refreshToken_normal(){
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        String newToken=jwtUtil.refreshToken(token);
        assertNotNull(newToken);
    }
    @Test
    void refreshToken_wrong_tokenIsNull(){
        String token="";
        assertThrows(AuthorizationException.class, ()->jwtUtil.refreshToken(token), "bad token");
    }
    @Test
    void refreshToken_wrong_tokenIsError(){
        String token="1fueiwfbewfu";
        assertThrows(AuthorizationException.class, ()->jwtUtil.refreshToken(token), "bad token");
    }
    @Test
     void refreshToken_wrong_tokenIsExpired(){
        String email="test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        String token= Constant.TOKEN_DEFAULT_SECRET_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()-10000000000L))
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getTokenSalt())
                .compact();
        assertDoesNotThrow(()->jwtUtil.refreshToken(token));
     }

    @Test
    void getEmailFromToken_normal(){
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        String newEmail=jwtUtil.getEmailFromToken(token);
        assertEquals(email,newEmail);
    }

    @Test
    void getEmailFromToken_wrong_tokenIsNull(){
        String token="";
        assertThrows(AuthorizationException.class, ()->jwtUtil.getEmailFromToken(token));
    }
    @Test
    void getEmailFromToken_wrong_tokenIsExpired(){
        String email="test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        String token= Constant.TOKEN_DEFAULT_SECRET_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()-10000000000L))
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getTokenSalt())
                .compact();
        assertNull(jwtUtil.getEmailFromToken(token));
    }


}