package org.hdstart.cloud.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Date;


@Slf4j
public class JwtUtils {

    //TODO 密钥（建议从配置文件读取，避免硬编码）
    private static final String SECRET = "AJNDJHSJKNSJDKJfhjkshfnkdjfhdjkfj";
    // 过期时间
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    // 生成Token
    public static String generateToken(String userId, String username) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.builder()
                .claim("username",username)         // 自定义声明（如用户ID、角色）
                .setSubject(userId)       // 主体标识（如用户名）
                .setIssuedAt(new Date())   // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)             // 签名算法自动推断为HS256
                .compact();
    }

    // 验证并解析Token
    public static Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

