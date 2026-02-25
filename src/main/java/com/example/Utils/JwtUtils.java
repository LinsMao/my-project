package com.example.Utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {
    //密钥
    private static final String SECRET = "GradPr0j2026!@#SecretKeyForJWT_HS512_ChangeThisWhenYouGoToProduction_123";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    //时效
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    /**
     * 生成Token
     */
    public static String generateToken(Long userId){
        Date now=new Date();
        Date expiryDate=new Date(now.getTime()+EXPIRATION);//现在时间加7天

        return Jwts.builder()
                .subject(userId.toString())               // 设置用户ID
                .issuedAt(now)                            // 签发时间
                .expiration(expiryDate)                   // 过期时间
                .signWith(KEY)                             // 使用密钥签名
                .compact();

    }

    /**
     * 解析Token
     */
    public static  Long getUserIdFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(KEY)                           // 验证签名
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }


    /**
     * 校验 Token 是否有效
     */
    public static boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch (JwtException | IllegalAccessError e){
            return false;
        }
    }

}































