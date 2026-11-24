package com.xmzs.coedit.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xmzs.coedit.config.OnlyOfficeConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT Token工具类
 * 用于OnlyOffice文档服务器的JWT认证
 *
 * @author zuleed
 * @date 2025-11-04
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final OnlyOfficeConfig onlyOfficeConfig;
    private final ObjectMapper objectMapper;

    /**
     * 生成JWT Token（默认1小时过期）
     *
     * @param payload 载荷数据
     * @return JWT Token字符串
     */
    public String generateToken(Map<String, Object> payload) {
        return generateToken(payload, 3600000L);
    }

    /**
     * 生成JWT Token（自定义过期时间）
     *
     * @param payload 载荷数据
     * @param ttlMillis 过期时间（毫秒）
     * @return JWT Token字符串
     */
    public String generateToken(Map<String, Object> payload, long ttlMillis) {
        if (!onlyOfficeConfig.getJwtEnabled()) {
            return null;
        }
        try {
            SecretKey key = getSecretKey();
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + Math.max(ttlMillis, 60_000L)); // 最少1分钟
            return Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        } catch (Exception e) {
            log.error("生成JWT Token失败", e);
            throw new RuntimeException("生成JWT Token失败", e);
        }
    }

    /**
     * 验证JWT Token
     *
     * @param token JWT Token字符串
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        if (!onlyOfficeConfig.getJwtEnabled()) {
            return true;
        }
        try {
            SecretKey key = getSecretKey();
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("验证JWT Token失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 解析JWT Token
     *
     * @param token JWT Token字符串
     * @return Claims对象
     */
    public Claims parseToken(String token) {
        if (!onlyOfficeConfig.getJwtEnabled()) {
            return null;
        }
        try {
            SecretKey key = getSecretKey();
            return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            log.error("解析JWT Token失败", e);
            throw new RuntimeException("解析JWT Token失败", e);
        }
    }

    /**
     * 从请求头中提取Token
     *
     * @param authHeader Authorization请求头
     * @return JWT Token字符串
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    /**
     * 生成文档配置的JWT Token
     *
     * @param config 文档配置对象
     * @return JWT Token字符串
     */
    public String generateDocumentToken(Object config) {
        if (!onlyOfficeConfig.getJwtEnabled()) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = objectMapper.convertValue(config, Map.class);
            return generateToken(configMap);
        } catch (Exception e) {
            log.error("生成文档配置JWT Token失败", e);
            throw new RuntimeException("生成文档配置JWT Token失败", e);
        }
    }

    /**
     * 获取密钥
     *
     * @return SecretKey对象
     */
    private SecretKey getSecretKey() {
        String secret = onlyOfficeConfig.getJwtSecret();
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT密钥未配置");
        }
        // 确保密钥长度足够（至少256位）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            // 如果密钥太短，使用HMAC-SHA256生成合适长度的密钥
            return Keys.hmacShaKeyFor(padSecret(secret).getBytes(StandardCharsets.UTF_8));
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 填充密钥到合适长度
     *
     * @param secret 原始密钥
     * @return 填充后的密钥
     */
    private String padSecret(String secret) {
        StringBuilder sb = new StringBuilder(secret);
        while (sb.length() < 32) {
            sb.append(secret);
        }
        return sb.substring(0, 32);
    }
}


