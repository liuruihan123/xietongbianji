package com.xmzs.test;


import com.xmzs.license.AppIdUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;

public class LicenseGenerator {
    public static void main(String[] args) throws Exception {
        // 1. 获取客户机器 MAC 地址（示例写死）
//        String clientId = "MAC:84:47:09:30:EC:91";
//        String clientId = "APP_ID:" + AppIdUtils.getAppId();
//        String clientId = "APP_ID:28b1b0f7-f222-4fcd-9e24-9eb7673bd3c8";
        String clientId = "APP_ID:0314060c-05ad-4647-9486-a24eb6ec5608";

        // 设置授权生效时间（手动设置）：2025-01-01 00:00:00
        LocalDateTime validFromDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        long now = validFromDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();

        // 设置具体的过期日期时间：2029-11-27 13:06:00
        LocalDateTime expireDateTime = LocalDateTime.of(2026, 11, 28, 13, 6, 0);
        long expire = expireDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();

        String dataJson = String.format(
                "{\"clientId\":\"%s\",\"validFrom\":%d,\"validTo\":%d}",
                clientId, now, expire
        );

        // 3. 加载私钥
        String privateKeyContent = new String(Files.readAllBytes(Paths.get("E:\\B\\private.pem")));
        privateKeyContent = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = kf.generatePrivate(spec);

        // 4. 签名
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(dataJson.getBytes());
        byte[] signatureBytes = sig.sign();
        String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);

        // 5. 生成 license.lic 文件
        String licenseContent = String.format(
                "{\n  \"data\": %s,\n  \"signature\": \"%s\"\n}",
                dataJson, signatureBase64
        );

        Files.write(Paths.get("license.lic"), licenseContent.getBytes());
        System.out.println("✅ 授权文件 license.lic 已生成！");
    }
}
