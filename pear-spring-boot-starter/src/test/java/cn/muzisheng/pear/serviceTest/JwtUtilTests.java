package cn.muzisheng.pear.serviceTest;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JwtUtilTests {


    private TokenProperties tokenProperties;
    @InjectMocks
    private JwtUtil jwtUtil;


    @Test
    void generateTokenWithEmail_normal() {
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        assertNotNull(token);
    }

    @Test
    void refreshToken_normal(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        String newToken=jwtUtil.refreshToken(token);
        assertNotNull(newToken);
    }
    @Test
    void refreshToken_wrong_tokenIsNull(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String token="";
        assertThrows(AuthorizationException.class, ()->jwtUtil.refreshToken(token), "bad token");
    }
    @Test
    void refreshToken_wrong_tokenIsError(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String token="1fueiwfbewfu";
        assertThrows(AuthorizationException.class, ()->jwtUtil.refreshToken(token), "bad token");
    }
    @Test
     void refreshToken_wrong_tokenIsExpired(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
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
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String email="test@example.com";
        String token=jwtUtil.generateTokenWithEmail(email);
        String newEmail=jwtUtil.getEmailFromToken(token);
        assertEquals(email,newEmail);
    }

    @Test
    void getEmailFromToken_wrong_tokenIsNull(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
        String token="";
        assertThrows(AuthorizationException.class, ()->jwtUtil.getEmailFromToken(token));
    }
    @Test
    void getEmailFromToken_wrong_tokenIsExpired(){
        TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
        jwtUtil=spy(new JwtUtil(tokenProperties));
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