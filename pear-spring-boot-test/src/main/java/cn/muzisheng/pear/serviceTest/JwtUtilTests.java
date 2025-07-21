package cn.muzisheng.pear.serviceTest;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.properties.TokenProperties;
import cn.muzisheng.pear.test.TestApplication;
import cn.muzisheng.pear.utils.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

@SpringBootTest(classes = TestApplication.class)
@ExtendWith(MockitoExtension.class)
class JwtUtilTests {

    @MockBean
    private TokenProperties tokenProperties;
    @InjectMocks
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("generateTokenWithEmail")
    class generateTokenWithEmail{
        @Test
        void generateTokenWithEmail_normal() {
            TokenProperties tokenProperties=new TokenProperties("secret", "Bearer ", 1000000000L);
            jwtUtil=spy(new JwtUtil(tokenProperties));
            String email="test@example.com";
            String token=jwtUtil.generateTokenWithEmail(email);
            assertNotNull(token);
        }
    }
    @Nested
    @DisplayName("refreshToken")
    class refreshToken{
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
            Assertions.assertThrows(AuthorizationException.class, ()->jwtUtil.refreshToken(token), "bad token");
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
                    .signWith(SignatureAlgorithm.HS512, tokenProperties.getSalt())
                    .compact();
            assertDoesNotThrow(()->jwtUtil.refreshToken(token));
        }
    }

    @Nested
    @DisplayName("getEmailFromToken")
    class getEmailFromToken {
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
                    .signWith(SignatureAlgorithm.HS512, tokenProperties.getSalt())
                    .compact();
            assertNull(jwtUtil.getEmailFromToken(token));
        }
    }
}