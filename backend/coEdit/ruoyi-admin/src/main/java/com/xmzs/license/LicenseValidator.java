package com.xmzs.license;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

//@Component
public class LicenseValidator {

    // 授权文件路径
    @Value("${license.file:config/license.lic}")
    private String licenseFilePath;

    // 公钥
    private static final String PUBLIC_KEY_BASE64 =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoO7hGcsWPb8T8+NhqPgb\n" +
                    "b6WHp9gF+fnwS5fCRYBmyHxoproC+5qoGxNObySids+fc9laE4kQS5dadXcu0ydp\n" +
                    "L2KIuhbla559dlEL2wdgFSv8Jta68bxgsc4cSXOOQ3lNWa0n7wveZ+JoG5LnY8/d\n" +
                    "UNAzupUwuU6DKk8reZ/VD7hdwGa/VPKZR/8KkFJG61sHDHfXrrcM1eCrSEXHC3+c\n" +
                    "qJPe4BGLXSQasH0htTFAktQKGedH+cWUoVCIXGUgArWROQdaaCXxP9519CdP5PgI\n" +
                    "vQydfsh24zK4do5i59alEY6OidYnZwtSnY+vgkmbuplz254q8x5Br9NSHgJtRWHX\n" +
                    "MQIDAQAB";

    @PostConstruct
    public void validate() {
        try {
            System.out.println("开始授权验证..."+ licenseFilePath);
            // 1. 读取授权文件
            String content = new String(Files.readAllBytes(Paths.get(licenseFilePath)));
            ObjectMapper mapper = new ObjectMapper();
            License license = mapper.readValue(content, License.class);

            // 2. 验证签名
            String dataJson = mapper.writeValueAsString(license.getData());
            if (!verifySignature(dataJson, license.getSignature())) {
                throw new RuntimeException("❌ 授权文件签名无效！可能被篡改。");
            }

            // 3. 验证有效期
            long now = Instant.now().getEpochSecond();
            long validFrom = license.getData().getValidFrom();
            long validTo = license.getData().getValidTo();

            // 格式化日期时间，便于查看
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTimeStr = LocalDateTime.ofInstant(Instant.ofEpochSecond(now), ZoneId.systemDefault()).format(formatter);
            String validFromStr = LocalDateTime.ofInstant(Instant.ofEpochSecond(validFrom), ZoneId.systemDefault()).format(formatter);
            String validToStr = LocalDateTime.ofInstant(Instant.ofEpochSecond(validTo), ZoneId.systemDefault()).format(formatter);

            System.out.println("========== 授权时间验证 ==========");
            System.out.println("当前时间：" + currentTimeStr + " (时间戳: " + now + ")");
            System.out.println("授权生效时间：" + validFromStr + " (时间戳: " + validFrom + ")");
            System.out.println("授权过期时间：" + validToStr + " (时间戳: " + validTo + ")");

            if (now < validFrom) {
                throw new RuntimeException(String.format(
                    "❌ 授权尚未生效！\n" +
                    "   当前时间：%s\n" +
                    "   授权生效时间：%s\n" +
                    "   还需等待：%d 天 %d 小时",
                    currentTimeStr, validFromStr,
                    (validFrom - now) / (24 * 3600),
                    ((validFrom - now) % (24 * 3600)) / 3600
                ));
            }

            if (now > validTo) {
                throw new RuntimeException(String.format(
                    "❌ 授权已过期！\n" +
                    "   当前时间：%s\n" +
                    "   授权过期时间：%s\n" +
                    "   已过期：%d 天 %d 小时",
                    currentTimeStr, validToStr,
                    (now - validTo) / (24 * 3600),
                    ((now - validTo) % (24 * 3600)) / 3600
                ));
            }

            // 计算剩余有效期
            long remainingDays = (validTo - now) / (24 * 3600);
            System.out.println("✅ 授权在有效期内，剩余有效期：" + remainingDays + " 天");
            System.out.println("================================");

            // 4. 验证客户端标识（MAC 地址）
//            String currentMac = getCurrentMacAddress();
//            if (!license.getData().getClientId().equals("MAC:" + currentMac)) {
//                throw new RuntimeException("❌ 授权不适用于当前设备。");
//            }
            if (!license.getData().getClientId().equals("APP_ID:" + getCurrentAppId())) {
                throw new RuntimeException("❌ 授权不适用于当前设备。");
            }

            System.out.println("✅ 授权验证通过，系统正常启动。");

        } catch (Exception e) {
            System.err.println("🛑 授权验证失败：" + e.getMessage());
            System.exit(1); // 强制退出
        }
    }

    private boolean verifySignature(String data, String signatureBase64) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLIC_KEY_BASE64.replaceAll("\\s", ""));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        return sig.verify(signatureBytes);
    }

    private String getCurrentMacAddress() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        List<NetworkIF> networkIFs = hal.getNetworkIFs();
        for (NetworkIF nif : networkIFs) {
            if (!nif.getMacaddr().isEmpty() && !nif.getMacaddr().equals("00:00:00:00:00:00")) {
                return nif.getMacaddr().toUpperCase();
            }
        }
        throw new RuntimeException("无法获取有效的 MAC 地址");
    }

    private String getCurrentAppId() {
        String appId = AppIdUtils.getAppId();
        System.out.println("当前appId：" + appId);
        return appId;
    }
}
