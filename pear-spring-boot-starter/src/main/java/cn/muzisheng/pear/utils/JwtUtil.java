package cn.muzisheng.pear.utils;

import cn.muzisheng.pear.constant.Constant;
import cn.muzisheng.pear.exception.AuthorizationException;
import cn.muzisheng.pear.properties.TokenProperties;
import io.jsonwebtoken.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Resource(name = "tokenProperties")
    private TokenProperties tokenProperties;

    /**
     * 根据用户email生成token
     */
    public String generateTokenWithEmail(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        return Constant.TOKEN_DEFAULT_SECRET_PREFIX +Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getTokenSalt())
                .compact();
    }

    /**
     * 刷新token过期时间
     * @throws AuthorizationException bad token
     */
    public String refreshToken(String token) {
        Claims claims;
        try{
            claims = getClaimFromToken(token);
        }catch (ExpiredJwtException e){
            claims=e.getClaims();
        }
        return generateTokenWithClaims(claims);
    }

    /**
     * 从token中获取登录用户名,如果返回null,说明token已经过期
     * @throws AuthorizationException bad token
     */
    public String getEmailFromToken(String token){
        Claims claims;
        try{
            claims = getClaimFromToken(token);
        }
        catch (ExpiredJwtException e){
            return null;
        }
        // 获取email
        return claims.get("email",String.class);

    }

    public String delTokenPrefix(String token){
        return token.replace(Constant.TOKEN_DEFAULT_SECRET_PREFIX,"");
    }
    /**
     * 从token中获取Claim
     * @throws AuthorizationException bad token
     */
    private Claims getClaimFromToken(String token) {
        Claims claims;
        try {
            claims=Jwts.parser()
                    .setSigningKey(tokenProperties.getTokenSalt())
                    .parseClaimsJws(delTokenPrefix(token))
                    .getBody();
        }catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException e){
            throw new AuthorizationException("bad token",e);
        }
        return claims;

    }

    /**
     * 根据claim生成token
     */
    private String generateTokenWithClaims(Claims claims) {
        return Constant.TOKEN_DEFAULT_SECRET_PREFIX +Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getTokenSalt())
                .compact();
    }


    /**
     * 生成token的过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + tokenProperties.getTokenExpire());
    }
}
