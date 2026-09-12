package cn.muzisheng.pear;

import cn.muzisheng.pear.config.TokenConfig;
import cn.muzisheng.pear.exception.AuthorizationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {
    private static final int HS256_MIN_KEY_BYTES = 32;

    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 过期时间(单位:毫秒)
     */
    private final long JWT_EXPIRE;
    private final String JWT_HEAD;
    private final String JWT_ISSUE;
    private final String JWT_SUBJECT;
    private final SecretKey KEY;

    /**
     * 根据 TokenConfig 初始化 JWT 工具。
     **/
    public JwtUtil(TokenConfig tokenConfig) {
        this.JWT_EXPIRE = positiveOrDefault(tokenConfig.getExpire(), Constant.TOKEN_DEFAULT_EXPIRE_DAY);
        this.JWT_HEAD = textOrDefault(tokenConfig.getHead(), Constant.TOKEN_DEFAULT_SECRET_PREFIX);
        this.JWT_ISSUE = textOrDefault(tokenConfig.getIssue(), Constant.TOKEN_DEFAULT_ISSUER);
        this.JWT_SUBJECT = textOrDefault(tokenConfig.getSubject(), Constant.TOKEN_DEFAULT_SUBJECT);
        this.KEY = buildKey(textOrDefault(tokenConfig.getSecret(), Constant.TOKEN_DEFAULT_SALT));
    }

    /**
     * 根据配置的 secret 构建 HS256 签名密钥，支持 Base64 编码值或原始字符串。
     **/
    private SecretKey buildKey(String secret) {
        byte[] keyBytes;
        try {
            byte[] decoded = Base64.getDecoder().decode(secret);
            keyBytes = decoded.length >= HS256_MIN_KEY_BYTES ? decoded : secret.getBytes(StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        if (keyBytes.length < HS256_MIN_KEY_BYTES) {
            throw new JwtException("The token secret is too weak. HS256 requires at least 32 bytes.");
        }
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
    }

    /**
     * 根据用户 email 生成 token。
     */
    public String generateTokenWithEmail(String email) {
        String uuid=UUIDUtil.generateId();
        Date expireDate=Date.from(Instant.now().plusMillis(JWT_EXPIRE));
        String jwtStr=Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HmacSHA256")
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
     * 基于合法旧 token 刷新 token。
     **/
    public String refreshToken(String token) {
        String email = getEmailFromToken(token);
        return generateTokenWithEmail(email);
    }


    /**
     * 从 token 中获取登录邮箱。
     *
     * @throws AuthorizationException bad token or token expired
     */
    public String getEmailFromToken(String token){
        Jws<Claims> claimsJws = getClaimFromToken(token);
        Claims claims = claimsJws.getPayload();
        return claims.get("email", String.class);
    }

    /**
     * 删除配置的 token 前缀，前缀不匹配时拒绝认证。
     **/
    public String delTokenPrefix(String token){
        if (token == null || !token.startsWith(JWT_HEAD)) {
            throw new AuthorizationException("bad token prefix");
        }
        return token.substring(JWT_HEAD.length());
    }

    /**
     * 从 token 中获取 Claim，并校验 issuer 与 subject。
     *
     * @throws AuthorizationException bad token or token expired
     */
    private Jws<Claims> getClaimFromToken(String token) {
        Jws<Claims> claims;
        try {
        claims = Jwts.parser()
                .requireIssuer(JWT_ISSUE)
                .requireSubject(JWT_SUBJECT)
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(delTokenPrefix(token));
        }catch (ExpiredJwtException e){
            throw new AuthorizationException("token expired",e);
        }catch (JwtException | IllegalArgumentException e){
            throw new AuthorizationException("bad token",e);
        }
        return claims;
    }

    private String textOrDefault(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    private long positiveOrDefault(Long value, long defaultValue) {
        if (value == null || value <= 0) {
            return defaultValue;
        }
        return value;
    }
}
