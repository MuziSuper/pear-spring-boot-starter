package cn.muzisheng.pear;

import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.exception.AuthorizationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;


@Component
public class JwtUtil {

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 过期时间(单位:秒)
     */
    private final long JWT_EXPIRE;
    private final String JWT_HEAD;
    private final String JWT_ISSUE;
    private final String JWT_SUBJECT;
    private final SecretKey KEY;
    public JwtUtil(TokenConfig tokenConfig) {
        this.JWT_EXPIRE = tokenConfig.getExpire();
        this.JWT_HEAD = tokenConfig.getHead();
        this.JWT_ISSUE = tokenConfig.getIssue();
        this.JWT_SUBJECT = tokenConfig.getSubject();
        try {
            this.KEY = Jwts.SIG.HS256.key().build();
        }catch (WeakKeyException e){
            throw new JwtException("The salt is too weak.");
        }
    }
    /**
     * 根据用户email生成token
     */
    public String generateTokenWithEmail(String email) {
        String uuid=UUIDUtil.generateId();
        Date expireDate=Date.from(Instant.now().plusMillis(JWT_EXPIRE));
        String jwtStr=Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS256")
                .and()
                .claim("email", email)
                .id(uuid)
                .expiration(expireDate)
                .issuedAt(new Date())
                .subject(JWT_SUBJECT)
                .issuer(JWT_ISSUE)
                .signWith(KEY,ALGORITHM)
                .compact();
        return JWT_HEAD + jwtStr;
    }
    /**
     * 刷新token
     **/
    public String refreshToken(String token) {
     String email = getEmailFromToken(token);
     return getEmailFromToken(email);
    }


    /**
     * 从token中获取登录用户名,如果返回null,说明token已经过期
     * @throws AuthorizationException bad token or token expired
     */
    public String getEmailFromToken(String token){
        Jws<Claims> claimsJws = getClaimFromToken(token);
        Claims claims = claimsJws.getPayload();
        return claims.get("email", String.class);
    }
    /**
     * 删除token前缀
     **/
    public String delTokenPrefix(String token){
        return token.replace(Constant.TOKEN_DEFAULT_SECRET_PREFIX,"");
    }
    /**
     * 从token中获取Claim
     * @throws AuthorizationException bad token or token expired
     */
    private Jws<Claims> getClaimFromToken(String token) {
        Jws<Claims> claims;
        try {
        claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(delTokenPrefix(token));
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e){
            throw new AuthorizationException("bad token",e);
        }catch (ExpiredJwtException e){
            throw new AuthorizationException("token expired",e);
        }
        return claims;
    }


}
